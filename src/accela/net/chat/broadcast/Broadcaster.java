package accela.net.chat.broadcast;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Íø¶Î¹ã²¥
public class Broadcaster implements Closeable
{
	private BroadcastServer server = null;

	private ExecutorService executor = Executors.newCachedThreadPool();

	private SyncEar ear = null;

	public Broadcaster(int port, Ear ear) throws IOException
	{
		this.server = new BroadcastServer(port);
		this.ear = new SyncEar(ear);
	}

	public int getPort()
	{
		return this.server.getPort();
	}

	public Ear getEar()
	{
		return this.ear.getEar();
	}

	public void start()
	{
		executor.submit(new Listener());
	}

	public boolean isOpen()
	{
		return server.isOpen();
	}

	@Override
	public void close() throws IOException
	{
		server.close();
		executor.shutdownNow();
	}

	public void say(Object message) throws IOException
	{
		server.send(message);
	}

	private class Listener implements Runnable
	{
		@Override
		public void run()
		{
			while (server.isOpen())
			{
				try
				{
					ear.onHear(server.receive());
				}
				catch (IOException ex)
				{
					if (server.isOpen())
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public static interface Ear
	{
		public void onHear(Object message);
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
		public synchronized void onHear(Object message)
		{
			assert (message != null);
			if (ear != null)
			{
				ear.onHear(message);
			}
		}

	}
}
