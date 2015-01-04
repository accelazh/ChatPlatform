package accela.net.chat.message.test;

import java.io.IOException;

import accela.net.chat.message.MessageServer;
import accela.net.chat.message.Messager;

public class TestMessageServer
{
	public static void main(String[] args) throws IOException
	{
		MessageServer s = new MessageServer(7890);
		assert(s.isOpen());
		Messager m = s.accept();
		assert(s.isOpen());
		assert(m.isOpen());
		assert (0 == m.getTimeOut());
		Block b = new Block(1, "server side");

		Block bRecv = (Block) m.receive();
		Block bRecv2 = (Block) m.receive();
		Block bRecv3 = (Block) m.receive();
		assert (bRecv != b);
		assert (bRecv2 != bRecv);
		assert (bRecv3 != bRecv2);
		assert (bRecv.a == 2);
		assert (bRecv.s.equals("client side"));
		assert (bRecv2.a == 3);
		assert (bRecv3.a == 3);
		assert (bRecv3.s.equals("client side 2"));

		m.send(b);
		m.send(b);

		System.out.println(m.toString());

		// ====================================================

		m = s.accept();
		System.out.println(m.toString());
	}
}
