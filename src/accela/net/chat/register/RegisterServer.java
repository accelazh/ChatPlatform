package accela.net.chat.register;

import java.io.Closeable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import accela.net.chat.common.MemberID;
import accela.net.chat.message.MessageServer;
import accela.net.chat.message.Messager;

//这个服务器用来记录一个聊天室中，有哪些成员。新成员加入聊天室时，
//需要在这个服务器中登记。聊天室中的成员互相通信时，通过这个服务器
//得到对方的MemberID，以建立连接
public class RegisterServer implements Closeable
{
	public static final long TIME_OUT = Register.TIME_OUT;

	private ExecutorService executor = Executors.newCachedThreadPool();

	private Register register = new Register();

	private MessageServer server = null;

	private boolean open = false;

	public RegisterServer(int port) throws IOException
	{
		server = new MessageServer(port);
	}

	public void start()
	{
		open = true;
		executor.submit(new Listener());
	}

	private class Listener implements Runnable
	{
		@Override
		public void run()
		{
			while (open)
			{
				try
				{
					executor.submit(new Worker(server.accept()));
				}
				catch (IOException ex)
				{
					if(open)
					{
						ex.printStackTrace();
					}
				}
			}

		}
	}

	private class Worker implements Runnable
	{
		private Messager messager = null;

		public Worker(Messager messager)
		{
			if (null == messager)
			{
				throw new NullPointerException("messager is null");
			}

			this.messager = messager;
		}

		@Override
		public void run()
		{
			final int TIME_OUT = 10000;
			try
			{
				messager.setTimeOut(TIME_OUT);
				RegisterMessage<?, ?> message = (RegisterMessage<?, ?>) messager
						.receive();
				message.handle(messager.getRemoteID(), register);
				messager.send(message);
			}
			catch (ClassCastException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				try
				{
					messager.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	public MemberID getLocalID()
	{
		return server.getLocalID();
	}

	public boolean isOpen()
	{
		return open;
	}

	@Override
	public void close() throws IOException
	{
		open = false;
		server.close();
		executor.shutdownNow();
	}

	@Override
	public String toString()
	{
		return "RegisterServer [register="
				+ register
				+ ", server="
				+ server
				+ ", open="
				+ open
				+ "]";
	}

}
