package accela.net.chat.register.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Set;

import accela.net.chat.common.MemberID;
import accela.net.chat.message.MessageServer;
import accela.net.chat.register.RegisterClient;

public class TestClient
{
	public static void main(String[] args) throws IOException
	{
		MessageServer server = new MessageServer(
				10000 + (int) (Math.random() * 10000));
		RegisterClient client = new RegisterClient(new MemberID(
				InetAddress.getLocalHost(), 7890), server);
		client.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = System.out;
		while (true)
		{
			out.print("<command> ");
			String command = in.readLine().trim().toLowerCase();
			out.println(handleCommand(server.getLocalID(), command, client));
		}
	}

	private static String handleCommand(MemberID me,
			String command,
			RegisterClient client) throws IOException
	{
		String[] tokens = command.split("\\s");

		MemberID para = null;
		if (tokens.length >= 3)
		{
			para = new MemberID(InetAddress.getByName(tokens[1]),
					Integer.parseInt(tokens[2]));
		}

		if (tokens[0].equals("howmanyregistered"))
		{
			return client.howManyRegistered() + "";
		}
		else if (tokens[0].equals("ismeregistered"))
		{
			return client.isMeRegistered() + "";
		}
		else if (tokens[0].equals("registerme"))
		{
			return client.registerMe() + "";
		}
		else if (tokens[0].equals("unregisterme"))
		{
			return client.unregisterMe() + "";
		}
		else if (tokens[0].equals("me"))
		{
			return me.toString();
		}
		else if (tokens[0].equals("isregistered"))
		{
			return client.isRegistered(para) + "";
		}
		else if (tokens[0].equals("whoareregistered"))
		{
			StringBuffer buf = new StringBuffer();
			Set<MemberID> ids = client.whoAreRegistered();
			for (MemberID id : ids)
			{
				buf.append(id.toString());
				buf.append("\n");
			}
			return buf.toString();
		}
		else if (tokens[0].equals("close"))
		{
			client.close();
			return "ok";
		}
		else
		{
			return "illegal command";
		}
	}
}
