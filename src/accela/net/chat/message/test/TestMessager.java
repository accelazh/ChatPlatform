package accela.net.chat.message.test;

import java.io.IOException;

import accela.net.chat.common.MemberID;
import accela.net.chat.message.MessageServer;
import accela.net.chat.message.Messager;

public class TestMessager
{
	public static void main(String[] args) throws IOException
	{
		MessageServer s = new MessageServer(7891);
		assert (s.isOpen());
		Messager m = s.connect(new MemberID(s.getLocalID().getAddr(), 7890));
		assert (m.isOpen());
		assert (0 == m.getTimeOut());
		Block b = new Block(2, "client side");
		m.send(b);
		b.a = 3;
		m.send(b);
		b.s = "client side 2";
		m.send(b);

		Block bRecv = (Block) m.receive();
		Block bRecv2 = (Block) m.receive();
		assert (bRecv != bRecv2);
		assert (bRecv.a == bRecv.a);
		assert (bRecv.s.equals(bRecv.s));

		System.out.println(m.toString());

		// ==============================================================

		m = s.connect(new MemberID(s.getLocalID().getAddr(), 7890));
		System.out.println(m.toString());

	}
}
