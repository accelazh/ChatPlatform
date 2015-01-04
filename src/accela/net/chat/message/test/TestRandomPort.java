package accela.net.chat.message.test;

import java.io.IOException;

import accela.net.chat.message.MessageServer;
import accela.net.chat.message.Messager;

public class TestRandomPort
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		MessageServer s = new MessageServer(0);
		System.out.println(s.toString());
		Messager m = s.accept();
		System.out.println(m);
	}

}
