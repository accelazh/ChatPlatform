package accela.net.chat.message;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import accela.net.chat.common.MemberID;

//除了基本功能外，MessageServer提供一个唯一的ID，这个类作为“聊天室”
//的网络底层，管理网络。Register服务器、成员之间的通信皆建立在它之上。
//聊天室的每一个成员，都会建立唯一一个MessageServer，管理底层通讯，并且
//为成员提供一个唯一的ID——MemberID
public class MessageServer implements Closeable
{
	private ServerSocket server = null;

	//传入port==0可使用任意的空闲端口
	public MessageServer(int port) throws IOException
	{
		this.server = new ServerSocket(port, 50, InetAddress.getLocalHost());
	}

	public MemberID getLocalID()
	{
		return new MemberID(server.getInetAddress(), server.getLocalPort());
	}

	public Messager accept() throws IOException
	{
		Socket client = server.accept();
		return new Messager(getLocalID(), client);
	}

	public Messager connect(MemberID remoteID) throws IOException
	{
		return new Messager(getLocalID(), remoteID);
	}

	public boolean isOpen()
	{
		return !server.isClosed();
	}
	
	@Override
	public void close() throws IOException
	{
		server.close();
	}

	@Override
	public String toString()
	{
		return "MessageServer [getLocalID()=" + getLocalID() + "]";
	}

}
