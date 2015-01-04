package accela.net.chat.register;

import accela.net.chat.common.MemberID;
import accela.net.chat.common.Message;

public abstract class RegisterMessage<RequestType, ReplyType> extends
		Message<RequestType, ReplyType>
{
	private static final long serialVersionUID = 1L;

	public RegisterMessage(RequestType request)
	{
		super(request);
	}

	protected void handle(MemberID requesterID, Register register)
	{
		setReply(handleImpl(requesterID, register));
	}

	protected abstract ReplyType handleImpl(MemberID requesterID, Register register);

}
