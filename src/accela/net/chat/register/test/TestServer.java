package accela.net.chat.register.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import accela.net.chat.register.RegisterServer;

public class TestServer
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{
		RegisterServer server = new RegisterServer(7890);
		server.start();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = System.out;
		while (true)
		{
			out.print("<command> ");
			String command = in.readLine().trim().toLowerCase();
			out.println(handleCommand(server, command));
		}
	}

	private static String handleCommand(RegisterServer server, String command)
			throws IOException
	{
		if (command.equals("show"))
		{
			return server.toString();
		}
		else if (command.equals("close"))
		{
			server.close();
			return "ok";
		}
		else
		{
			return "illegal command";
		}
	}
}
