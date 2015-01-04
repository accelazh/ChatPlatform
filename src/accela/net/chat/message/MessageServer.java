package accela.net.chat.message;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import accela.net.chat.common.MemberID;

//���˻��������⣬MessageServer�ṩһ��Ψһ��ID���������Ϊ�������ҡ�
//������ײ㣬�������硣Register����������Ա֮���ͨ�ŽԽ�������֮�ϡ�
//�����ҵ�ÿһ����Ա�����Ὠ��Ψһһ��MessageServer������ײ�ͨѶ������
//Ϊ��Ա�ṩһ��Ψһ��ID����MemberID
public class MessageServer implements Closeable
{
	private ServerSocket server = null;

	//����port==0��ʹ������Ŀ��ж˿�
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
