package accela.net.chat.broadcast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BroadcastServer implements Closeable
{
	public static final int MAX_MESSAGE_BYTES = 1024;

	private static final String GROUP_ADDRESS = "230.0.0.1";

	private int port = 0;

	private MulticastSocket socket = null;

	public BroadcastServer(int port) throws IOException
	{
		if (port < 0)
		{
			throw new IllegalArgumentException("port < 0");
		}

		this.port = port;
		socket = new MulticastSocket(port);
		InetAddress group = InetAddress.getByName(GROUP_ADDRESS);
		socket.joinGroup(group);
	}

	public int getPort()
	{
		return this.port;
	}

	public boolean isOpen()
	{
		return !socket.isClosed();
	}

	@Override
	public void close() throws IOException
	{
		socket.close();
	}

	private byte[] toBytes(Object message) throws IOException
	{
		assert (message != null);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(message);
			out.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		if (bout.size() > MAX_MESSAGE_BYTES)
		{
			throw new IOException("bytes of message exceeds MAX_MESSAGE_BYTS: "
					+ bout.size());
		}
		return bout.toByteArray();
	}

	private Object fromBytes(byte[] bytes, int offset, int length)
			throws IOException
	{
		assert (bytes != null);

		Object message = null;
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				bytes, offset, length));
		try
		{
			message = in.readObject();
		}
		catch (ClassNotFoundException ex)
		{
			throw new IOException(ex);
		}

		in.close();
		assert (message != null);
		return message;
	}

	public Object receive() throws IOException
	{
		DatagramPacket dataPacket = new DatagramPacket(
				new byte[MAX_MESSAGE_BYTES], MAX_MESSAGE_BYTES);

		socket.receive(dataPacket);
		return fromBytes(dataPacket.getData(),
				dataPacket.getOffset(),
				dataPacket.getLength());
	}

	public void send(Object message) throws IOException
	{
		if (null == message)
		{
			throw new NullPointerException("null message");
		}

		byte[] data = toBytes(message);
		socket.send(new DatagramPacket(data, data.length, InetAddress
				.getByName(GROUP_ADDRESS), port));
	}
}
