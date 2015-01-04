package accela.net.chat.common;

import java.io.Serializable;
import java.net.InetAddress;

public class MemberID implements Serializable
{
	private static final long serialVersionUID = 1L;

	private InetAddress addr = null;
	private int port = 0;

	public MemberID(InetAddress addr, int port)
	{
		if (null == addr)
		{
			throw new NullPointerException("null addr");
		}
		if (port < 0)
		{
			throw new IllegalArgumentException("port < 0");
		}

		this.addr = addr;
		this.port = port;
	}

	public InetAddress getAddr()
	{
		return addr;
	}

	public int getPort()
	{
		return port;
	}

	@Override
	public int hashCode()
	{
		return addr.hashCode() ^ port;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (null == obj || !(obj instanceof MemberID))
		{
			return false;
		}

		MemberID other = (MemberID) obj;
		return this.addr.equals(other.addr) && this.port == other.port;
	}

	@Override
	public String toString()
	{
		return "MemberID [addr=" + addr + ", port=" + port + "]";
	}

}
