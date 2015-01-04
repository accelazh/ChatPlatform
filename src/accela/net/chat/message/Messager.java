package accela.net.chat.message;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import accela.net.chat.common.MemberID;

//Messager对应一个Socket连接
public class Messager implements Closeable
{
	private final int HANDSHAKE_TIME_OUT = 10000;

	private Socket conn = null;

	private MemberID remoteID = null;

	private MemberID localID = null;

	private InputStream in = null;

	private OutputStream out = null;

	private AtomicLong lastActiveTime = new AtomicLong(
			System.currentTimeMillis());

	private Lock sendLock = new ReentrantLock();

	private Lock receiveLock = new ReentrantLock();

	protected Messager(MemberID localID, Socket conn) throws IOException
	{
		if (null == localID)
		{
			throw new NullPointerException("null localID");
		}
		if (null == conn)
		{
			throw new NullPointerException("null conn");
		}

		try
		{
			this.conn = conn;
			if (!isLocalIDMatched(localID))
			{
				throw new IOException("unmatched local id");
			}
			this.localID = localID;

			this.conn.setSoTimeout(HANDSHAKE_TIME_OUT);

			this.remoteID = receiveRemoteID();
			this.sendLocalID(localID);

			this.conn.setSoTimeout(0);

		}
		catch (IOException ex)
		{
			conn.close();
			throw ex;
		}
	}

	protected Messager(MemberID localID, MemberID remoteID) throws IOException
	{
		if (null == localID)
		{
			throw new NullPointerException("null localID");
		}
		if (null == remoteID)
		{
			throw new NullPointerException("remoteID");
		}

		try
		{
			this.remoteID = remoteID;
			this.conn = new Socket(remoteID.getAddr(), remoteID.getPort());
			if (!isLocalIDMatched(localID))
			{
				throw new IOException("unmatched local id");
			}
			this.localID = localID;

			this.conn.setSoTimeout(HANDSHAKE_TIME_OUT);
			this.sendLocalID(localID);

			MemberID receivedRemoteID = receiveRemoteID();
			if (!receivedRemoteID.equals(remoteID))
			{
				throw new IOException("unmatched received remote id");
			}

			this.conn.setSoTimeout(0);

		}
		catch (IOException ex)
		{
			if (conn != null)
			{
				conn.close();
			}
			throw ex;
		}
	}

	private ObjectInputStream getInputStream() throws IOException
	{
		if (null == in)
		{
			in = conn.getInputStream();
		}

		return new ObjectInputStream(in);
	}

	private ObjectOutputStream getOutputStream() throws IOException
	{
		if (null == out)
		{
			out = conn.getOutputStream();
		}

		return new ObjectOutputStream(out);
	}

	private Object receiveObject() throws IOException, ClassNotFoundException
	{
		return getInputStream().readObject();
	}

	private void sendObject(Object obj) throws IOException
	{
		getOutputStream().writeObject(obj);
	}

	private boolean isRemoteIDMatched(MemberID id)
	{
		return id.getAddr().equals(conn.getInetAddress());
	}

	private boolean isLocalIDMatched(MemberID id)
	{
		return id.getAddr().equals(conn.getLocalAddress());
	}

	private MemberID receiveRemoteID() throws IOException
	{
		MemberID id = null;
		try
		{
			id = (MemberID) receiveObject();
		}
		catch (ClassNotFoundException ex)
		{
			throw new IOException(ex);
		}
		catch (ClassCastException ex)
		{
			throw new IOException(ex);
		}

		if (!isRemoteIDMatched(id))
		{
			throw new IOException("unmatched remote id");
		}

		return id;
	}

	private void sendLocalID(MemberID id) throws IOException
	{
		if (null == id)
		{
			throw new NullPointerException("null id");
		}
		if (!isLocalIDMatched(id))
		{
			throw new IllegalArgumentException("unmatched local id");
		}

		sendObject(id);
	}

	public MemberID getRemoteID()
	{
		return this.remoteID;
	}

	public MemberID getLocalID()
	{
		return this.localID;
	}

	public int getTimeOut() throws IOException
	{
		return conn.getSoTimeout();
	}

	public void setTimeOut(int milliseconds) throws IOException
	{
		conn.setSoTimeout(milliseconds);
	}

	public Object receive() throws IOException
	{
		receiveLock.lock();
		try
		{
			Object obj = null;
			try
			{
				obj = receiveObject();
			}
			catch (ClassNotFoundException ex)
			{
				throw new IOException(ex);
			}

			assert (obj != null);
			refreshActiveTime();
			return obj;
		}
		finally
		{
			receiveLock.unlock();
		}
	}

	public void send(Object obj) throws IOException
	{
		if (null == obj)
		{
			throw new NullPointerException("null obj");
		}

		sendLock.lock();
		try
		{
			sendObject(obj);
			refreshActiveTime();
		}
		finally
		{
			sendLock.unlock();
		}
	}

	public boolean isOpen()
	{
		return !conn.isClosed();
	}

	@Override
	public void close() throws IOException
	{
		conn.close();
	}

	private void refreshActiveTime()
	{
		this.lastActiveTime.set(System.currentTimeMillis());
	}

	public long getLastActiveTime()
	{
		return lastActiveTime.get();
	}

	@Override
	public String toString()
	{
		int timeOut = -1;
		try
		{
			timeOut = getTimeOut();
		}
		catch (IOException ex)
		{
			timeOut = -1;
		}

		return "Messager [getRemoteID()="
				+ getRemoteID()
				+ ", getLocalID()="
				+ getLocalID()
				+ ", getTimeOut()="
				+ (timeOut < 0 ? "error" : timeOut)
				+ "]";
	}

}
