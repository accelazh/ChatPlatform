package accela.net.chat.broadcast.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import accela.net.chat.broadcast.Broadcaster;

public class TestBroadcaster
{
	private static Broadcaster broadcaster = null;

	static
	{
		try
		{
			broadcaster = new Broadcaster(8172, new TesterEar());
			broadcaster.start();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private static class TesterEar implements Broadcaster.Ear
	{
		@Override
		public void onHear(Object message)
		{
			System.out.println("heard: " + message);
		}
	}

	public static void main(String[] args) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = System.out;
		while (true)
		{
			out.print("<command> ");
			String cmdLine = in.readLine().trim();
			if (cmdLine.length() <= 0)
			{
				continue;
			}

			String cmd = "";
			String arg = "";
			int idx = cmdLine.indexOf(' ');
			if (idx < 0)
			{
				cmd = cmdLine;
			}
			else if (0 == idx)
			{
				assert (false);
			}
			else
			{
				cmd = cmdLine.substring(0, idx).trim();
				arg = cmdLine.substring(idx + 1).trim();
			}

			out.println(handleCommand(cmd, arg));
		}
	}

	private static String handleCommand(String cmd, String arg)
			throws IOException
	{
		if (cmd.equals("say"))
		{
			broadcaster.say(arg);
			return "said: "+arg;
		}
		else if(cmd.equals("close"))
		{
			broadcaster.close();
			return "closed";
		}
		else
		{
			return "illegal command";
		}
	}

}
