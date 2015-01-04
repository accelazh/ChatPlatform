package accela.net.chat.register.protocol;

import accela.net.chat.common.MemberID;
import accela.net.chat.register.Register;
import accela.net.chat.register.RegisterMessage;

public class IsRegistered extends RegisterMessage<MemberID, Boolean>
{
	private static final long serialVersionUID = 1L;

	public IsRegistered(MemberID who)
	{
		super(who);
	}

	@Override
	protected Boolean handleImpl(MemberID requesterID, Register register)
	{
		if (null == getRequest())
		{
			return register.contains(requesterID);
		}
		return register.contains(getRequest());
	}

}
