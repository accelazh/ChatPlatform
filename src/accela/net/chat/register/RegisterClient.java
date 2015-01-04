package accela.net.chat.register;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import accela.net.chat.common.MemberID;
import accela.net.chat.message.MessageServer;
import accela.net.chat.message.Messager;
import accela.net.chat.register.protocol.HowManyRegistered;
import accela.net.chat.register.protocol.IsRegistered;
import accela.net.chat.register.protocol.KeepMeRegistered;
import accela.net.chat.register.protocol.RegisterMe;
import accela.net.chat.register.protocol.UnregisterMe;
import accela.net.chat.register.protocol.WhoAreRegistered;

public class RegisterClient implements Closeable
{
	private MessageServer clientServer = null;

	private MemberID registerServerID = null;

	private Timer timer = null;

	private boolean open = false;

	public RegisterClient(MemberID registerServerID, MessageServer clientServer)
	{
		if (null == registerServerID)
		{
			throw new NullPointerException("registerServerID null");
		}
		if (null == clientServer)
		{
			throw new NullPointerException("clientServer null");
		}

		this.registerServerID = registerServerID;
		this.clientServer = clientServer;
	}

	public void start()
	{
		open = true;
		timer = new Timer();
		timer.schedule(new RegisterKeeper(), 0, RegisterServer.TIME_OUT / 2);
	}

	public boolean isOpen()
	{
		return open;
	}

	@Override
	public void close() throws IOException
	{
		open = false;
		timer.cancel();
	}

	public MessageServer getClientServer()
	{
		return clientServer;
	}

	public MemberID getRegisterServerID()
	{
		return registerServerID;
	}

	private Messager getConnection() throws IOException
	{
		final int TIME_OUT = 10000;

		Messager messager = clientServer.connect(getRegisterServerID());
		messager.setTimeOut(TIME_OUT);
		return messager;
	}

	@SuppressWarnings("unchecked")
	private <T extends RegisterMessage<?, ?>> T requestRegisterServer(T request)
			throws IOException
	{
		assert (request != null);

		Messager messager = getConnection();
		try
		{
			messager.send(request);
			T result = null;
			try
			{
				result = (T) messager.receive();
			}
			catch (ClassCastException ex)
			{
				throw new IOException(ex);
			}

			assert (result != null);
			return result;
		}
		finally
		{
			messager.close();
		}
	}

	public int howManyRegistered() throws IOException
	{
		return requestRegisterServer(new HowManyRegistered()).getReply();
	}

	public boolean isRegistered(MemberID memberID) throws IOException
	{
		return requestRegisterServer(new IsRegistered(memberID)).getReply();
	}

	public boolean isMeRegistered() throws IOException
	{
		return isRegistered(null);
	}

	public boolean registerMe() throws IOException
	{
		return requestRegisterServer(new RegisterMe()).getReply();
	}

	public boolean unregisterMe() throws IOException
	{
		return requestRegisterServer(new UnregisterMe()).getReply();
	}

	private boolean keepMeRegistered() throws IOException
	{
		return requestRegisterServer(new KeepMeRegistered()).getReply();
	}

	public Set<MemberID> whoAreRegistered() throws IOException
	{
		return requestRegisterServer(new WhoAreRegistered()).getReply();
	}

	private class RegisterKeeper extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				keepMeRegistered();
			}
			catch (IOException ex)
			{
				if (open)
				{
					ex.printStackTrace();
				}
			}
		}
	}

}
