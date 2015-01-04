package accela.net.chat.chatter;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import accela.net.chat.common.MemberID;
import accela.net.chat.message.MessageServer;
import accela.net.chat.message.Messager;
import accela.net.chat.register.RegisterClient;

//�����ҵ�һ����Ա����ӵ��Ψһ��ID����MemberID�������Լ���
public class Chatter implements Closeable
{
	public static final long CONNECTION_TIME_OUT = 10000;

	private MessageServer clientServer = null;

	private RegisterClient registerClient = null;

	private ConcurrentMap<MemberID, Messager> connectionPool = new ConcurrentHashMap<MemberID, Messager>();

	private ExecutorService executor = Executors.newCachedThreadPool();

	private SyncEar ear = null;

	private boolean open = false;

	// ����port==0��ʹ������Ŀ��ж˿�
	public Chatter(int localPort, MemberID registerServerID, Ear ear)
			throws IOException
	{
		clientServer = new MessageServer(localPort);
		registerClient = new RegisterClient(registerServerID, clientServer);

		this.ear = new SyncEar(ear);
	}

	public MemberID getLocalID()
	{
		return clientServer.getLocalID();
	}

	public MemberID getRegisterServerID()
	{
		return registerClient.getRegisterServerID();
	}

	public Ear getEar()
	{
		return ear.getEar();
	}

	public void start() throws IOException
	{
		open = true;
		registerClient.start();
		registerClient.registerMe();
		executor.submit(new Accepter());
		executor.submit(new Cleaner());
	}

	public boolean isOpen()
	{
		return open;
	}

	@Override
	public void close() throws IOException
	{
		open = false;
		registerClient.unregisterMe();
		registerClient.close();
		clientServer.close();
		executor.shutdownNow();

		List<Messager> messagers = new LinkedList<Messager>(
				connectionPool.values());
		connectionPool.clear();
		for (Messager messager : messagers)
		{
			messager.close();
		}
	}

	private Messager createConnection(MemberID toWhom) throws IOException
	{
		// System.out.println("chatter "
		// + getLocalID()
		// + " creating a connection to "
		// + toWhom);
		return clientServer.connect(toWhom);
	}

	private Messager fetchConnection(MemberID toWhom) throws IOException
	{
		Messager messager = connectionPool.remove(toWhom);
		if (messager != null && messager.isOpen())
		{
			return messager;
		}

		messager = createConnection(toWhom);
		return messager;
	}

	private void putConnection(Messager messager) throws IOException
	{
		if (!messager.isOpen())
		{
			return;
		}

		Messager old = connectionPool.put(messager.getRemoteID(), messager);
		executor.submit(new Listener(messager));

		assert (old != messager);
		if (old != null)
		{
			old.close();
		}
	}

	public void say(MemberID toWhom, Object message) throws IOException
	{
		if (null == toWhom || null == message)
		{
			throw new NullPointerException("toWhom or message is null");
		}

		if (toWhom.equals(this.getLocalID()))
		{
			// System.out.println(this.getLocalID()
			// + " sends loop message to him self");

			this.ear.onHear(toWhom, message);
			return;
		}

		IOException exception = null;
		// trick: Ϊ�˷�ֹfetch����connection��ʹ��ʱ��Զ�˹رգ�
		// ��������ʧ�ܣ������������
		for (int retry = 0; retry < 2; retry++)
		{
			Messager messager = null;
			try
			{
				messager = fetchConnection(toWhom);
				// trick:
				// fetch-send-put������ʽ��1.ʹ��messager�ڷ�����Ϣʱ��
				// ����Cleaner�߳�Ӱ��; 2.ʧЧ��messager������ص�
				// connectionPool��
				messager.send(message);
				putConnection(messager);

				break;
			}
			catch (IOException ex)
			{
				if (messager != null)
				{
					try
					{
						messager.close();
					}
					catch (IOException ex_inner)
					{
						ex_inner.printStackTrace();
					}
				}
				exception = ex;
			}
		}
		if (exception != null)
		{
			throw exception;
		}
	}

	public boolean isMember(MemberID who) throws IOException
	{
		return registerClient.isRegistered(who);
	}

	public int countMembers() throws IOException
	{
		return registerClient.howManyRegistered();
	}

	// ���صļ����У��������Chatter�Լ���������RegisterServer��MemberID
	public Set<MemberID> members() throws IOException
	{
		return registerClient.whoAreRegistered();
	}

	private class Accepter implements Runnable
	{
		@Override
		public void run()
		{
			while (clientServer.isOpen())
			{
				try
				{
					Messager messager = clientServer.accept();
					putConnection(messager);
				}
				catch (IOException ex)
				{
					if (clientServer.isOpen())
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}

	private class Listener implements Runnable
	{
		private Messager messager = null;

		public Listener(Messager messager)
		{
			assert (messager != null);
			this.messager = messager;
		}

		@Override
		public void run()
		{
			while (messager.isOpen())
			{
				Object message = null;
				try
				{
					message = messager.receive();
					ear.onHear(messager.getRemoteID(), message);
				}
				catch (EOFException ex)
				{
					try
					{
						messager.close();
					}
					catch (IOException ex_inner)
					{
						ex_inner.printStackTrace();
					}
				}
				catch (IOException ex)
				{
					if (messager.isOpen())
					{
						ex.printStackTrace();
					}
				}
			}
			connectionPool.remove(messager.getRemoteID());
		}
	}

	private class Cleaner implements Runnable
	{
		@Override
		public void run()
		{
			while (open)
			{
				Iterator<Map.Entry<MemberID, Messager>> itr = connectionPool
						.entrySet().iterator();
				long curTime = System.currentTimeMillis();
				while (itr.hasNext())
				{
					Map.Entry<MemberID, Messager> entry = itr.next();
					Messager messager = entry.getValue();
					assert (messager.getRemoteID().equals(entry.getKey()));

					if (curTime - messager.getLastActiveTime() >= CONNECTION_TIME_OUT)
					{
						itr.remove();
						try
						{
							messager.close();
						}
						catch (IOException ex)
						{
							ex.printStackTrace();
						}

						// System.out.println("chatter "
						// + getLocalID()
						// + " cleaned the connection to "
						// + messager.getRemoteID());
					}
				}

				try
				{
					Thread.sleep(CONNECTION_TIME_OUT);
				}
				catch (InterruptedException ex)
				{
					if (open)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public static interface Ear
	{
		public void onHear(MemberID fromWhom, Object message);
	}

	private static class SyncEar implements Ear
	{
		private Ear ear = null;

		public SyncEar(Ear ear)
		{
			this.ear = ear;
		}

		public Ear getEar()
		{
			return ear;
		}

		@Override
		public synchronized void onHear(MemberID fromWhom, Object message)
		{
			assert (fromWhom != null);
			assert (message != null);
			if (ear != null)
			{
				ear.onHear(fromWhom, message);
			}
		}

	}

	@Override
	public String toString()
	{
		return "Chatter [clientServer="
				+ clientServer
				+ ", registerClient="
				+ registerClient
				+ ", connectionPool="
				+ connectionPool
				+ ", open="
				+ open
				+ ", getLocalID()="
				+ getLocalID()
				+ ", getRegisterServerID()="
				+ getRegisterServerID()
				+ "]";
	}

}
