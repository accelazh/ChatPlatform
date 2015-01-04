package accela.net.chat;

import java.io.IOException;

import accela.net.chat.broadcast.Broadcaster;
import accela.net.chat.chatter.Chatter;
import accela.net.chat.common.MemberID;
import accela.net.chat.register.RegisterServer;

/* 新建聊天室的步骤：
 * 1. 新建一个RegisterServer。这是一个服务器，在其上进行所有聊天室成员的登记
 * 2. 新建Chatter。这是聊天室成员。新建Chatter时，需要指定聊天室所用的RegisterServer。
 *    Chatter会自动把自己注册到RegisterServer上。需要知道同一个聊天室中其它Chatter时，
 *    可以调用Chatter.members()方法，来取得RegisterServer上已经登记的所有Chatter名单。
 * 3. Chatter之间互相发送消息
 * 4. Chatter.close()方法离开聊天室，并关闭Chatter。一个Chatter对象生命周期中，只能与
 *    一个聊天室，即一个RegisterServer绑定。因此Chatter不能更换聊天室，除非新建一个Chatter
 *    对象。
 *    
 * Note:
 * 1. RegisterServer、Chatter、Broadcaster需要先调用start()方法，打开后，才能够使用。
 *    用完后，调用close()方法关闭。
 * 2. Broadcaster完全是可选的，与聊天室没有耦合。你可以使用它在同一网段内发送和接收广播。
 * 3. MemberID是用于标识网络节点身份的。通过它，可以区分不同的网络节点，也可以向相应节点
 *    建立连接。聊天室的各个成员之间，使用MemberID标识身份。RegisterServer也使用MemberID标识
 *    自己的身份，尽管它不是聊天室成员；通过RegisterServer的MemberID，可以建立与之的连接。
 */
public class ChatterFactory
{
	private ChatterFactory()
	{
		// do nothing
	}

	public static RegisterServer createRegisterServer() throws IOException
	{
		return new RegisterServer(0);
	}

	public static Chatter createChatter(MemberID registerServerID,
			Chatter.Ear ear) throws IOException
	{
		return new Chatter(0, registerServerID, ear);
	}

	public static Broadcaster createBroadcaster(Broadcaster.Ear ear)
			throws IOException
	{
		final int port = 8172;
		return new Broadcaster(port, ear);
	}

}
