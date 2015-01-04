package accela.net.chat.broadcast.test;

import java.io.IOException;

import accela.net.chat.broadcast.BroadcastServer;

public class TestBroadcastServerReceiver
{
	public static void main(String[] args) throws IOException
	{
		BroadcastServer server=new BroadcastServer(1172);
		System.out.println(server.receive());
		System.out.println(server.receive());
		server.send("hello, this is a broadcast receiver");
		server.close();
	}

}
