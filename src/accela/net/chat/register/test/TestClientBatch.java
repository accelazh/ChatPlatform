package accela.net.chat.register.test;

import java.io.IOException;
import java.net.InetAddress;

import accela.net.chat.common.MemberID;
import accela.net.chat.message.MessageServer;
import accela.net.chat.register.RegisterClient;

public class TestClientBatch
{
	public static void main(String[] args) throws IOException
	{
		MessageServer server = new MessageServer(
				10000 + (int) (Math.random() * 10000));
		RegisterClient client = new RegisterClient(new MemberID(
				InetAddress.getLocalHost(), 7890), server);
		client.start();

		for (int i = 0; i < 100; i++)
		{
			System.out.println("i: " + i);

			client.registerMe();
			assert(client.isMeRegistered());
			assert(client.isRegistered(server.getLocalID()));
			assert(client.howManyRegistered()>=1);
			assert(client.whoAreRegistered().size()>=1);
			client.unregisterMe();
		}
	}

}
