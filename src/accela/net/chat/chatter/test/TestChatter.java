package accela.net.chat.chatter.test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import accela.net.chat.chatter.Chatter;
import accela.net.chat.common.MemberID;
import accela.net.chat.register.RegisterServer;

public class TestChatter
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{
		final int NUM_CHATTER = 32;

		RegisterServer registerServer = new RegisterServer(0);
		registerServer.start();
		System.out.println(registerServer.getLocalID());

		Map<Chatter, TesterEar> chatters = new HashMap<Chatter, TesterEar>();
		for (int i = 0; i < NUM_CHATTER; i++)
		{
			TesterEar ear = new TesterEar();
			Chatter chatter = new Chatter(0, registerServer.getLocalID(), ear);
			chatter.start();
			chatters.put(chatter, ear);

			System.out.println(chatter.getLocalID());
		}

		// ====test 1: 获得成员列表====
		Chatter chatterA = new Chatter(0, registerServer.getLocalID(),
				new TesterEar());
		chatterA.start();
		assert (chatterA.isMember(null));

		for (Chatter c : chatters.keySet())
		{
			System.out.println("testing " + c + "...");
			assert (c.isMember(null));
			for (Chatter c_inner : chatters.keySet())
			{
				assert (c.isMember(c_inner.getLocalID()));
			}
			assert (c.isMember(chatterA.getLocalID()));
			assert (c.countMembers() == NUM_CHATTER + 1);
			Set<MemberID> members = c.members();
			for (Chatter c_inner : chatters.keySet())
			{
				assert (members.contains(c_inner.getLocalID()));
			}
			assert (c.members().contains(chatterA.getLocalID()));
		}

		// ====test 2: 有成员离开后，获得成员列表====
		chatterA.close();
		assert (!chatterA.isMember(null));

		for (Chatter c : chatters.keySet())
		{
			System.out.println("testing " + c + "...");
			assert (c.isMember(null));
			for (Chatter c_inner : chatters.keySet())
			{
				assert (c.isMember(c_inner.getLocalID()));
			}
			assert (!c.isMember(chatterA.getLocalID()));
			assert (c.countMembers() == NUM_CHATTER);
			Set<MemberID> members = c.members();
			for (Chatter c_inner : chatters.keySet())
			{
				assert (members.contains(c_inner.getLocalID()));
			}
			assert (!c.members().contains(chatterA.getLocalID()));
		}

		// ====test 3: 互相通信（观察连接的新建情况、复用情况、回收情况）====
		for (Chatter c : chatters.keySet())
		{
			for (Chatter c_inner : chatters.keySet())
			{
				c.say(c_inner.getLocalID(),
						c.toString() + " ====>> " + c_inner.toString());
				c_inner.say(c.getLocalID(),
						c_inner.toString() + " ====>> " + c.toString());
			}
		}
		for (Chatter c : chatters.keySet())
		{
			System.out.println(c + " is counting message...");

			TesterEar e = chatters.get(c);
			for (Chatter c_inner : chatters.keySet())
			{
				int count = 0;
				for (Message m : e.getMessages())
				{
					assert (m.message != null);
					if (m.fromWhom.equals(c_inner.getLocalID()))
					{
						count++;
					}
				}
				assert (2 == count); // 似乎可能会因为网络忙，而丢失数据包，导致该条件不满足
			}
			assert (e.getMessages().size() == NUM_CHATTER * 2);
		}

		System.out.println("\n====cross communication finished====!\n");

		Thread.sleep(Chatter.CONNECTION_TIME_OUT + 2);

		// ====关闭服务器====
		for (Chatter c : chatters.keySet())
		{
			c.close();
		}
		registerServer.close();
		System.out.println("====Test Finished====");
	}

	private static class Message
	{
		public MemberID fromWhom = null;

		public Object message = null;

		public Message(MemberID fromWhom, Object message)
		{
			this.fromWhom = fromWhom;
			this.message = message;
		}
	}

	private static class TesterEar implements Chatter.Ear
	{
		private List<Message> messages = new LinkedList<Message>();

		@Override
		public void onHear(MemberID fromWhom, Object message)
		{
			assert (fromWhom != null);
			assert (message != null);
			messages.add(new Message(fromWhom, message));
		}

		public List<Message> getMessages()
		{
			return Collections.unmodifiableList(messages);
		}
	}

}
