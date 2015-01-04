package accela.net.chat.common;

import java.io.Serializable;

public class Message<RequestType, ReplyType> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private RequestType request = null;

	private ReplyType reply = null;

	private Throwable exception = null;

	public Message(RequestType request)
	{
		this.request = request;
	}

	public RequestType getRequest()
	{
		return request;
	}

	public ReplyType getReply()
	{
		return reply;
	}

	public void setReply(ReplyType reply)
	{
		this.reply = reply;
	}

	public Throwable getException()
	{
		return exception;
	}

	public void setException(Throwable exception)
	{
		this.exception = exception;
	}

	@Override
	public String toString()
	{
		return "Message [request=" + request + ", reply=" + reply
				+ ", exception=" + exception + "]";
	}

}
