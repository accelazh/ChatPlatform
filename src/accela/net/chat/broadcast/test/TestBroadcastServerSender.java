package accela.net.chat.broadcast.test;

import java.io.IOException;

import accela.net.chat.broadcast.BroadcastServer;

public class TestBroadcastServerSender
{
	public static void main(String[] args) throws IOException
	{
		BroadcastServer server=new BroadcastServer(1172);
		server.send("hello, this is a broadcaster!");
		System.out.println(server.receive());
		server.close();
	}

}
