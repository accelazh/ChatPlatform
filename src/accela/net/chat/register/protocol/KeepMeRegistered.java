package accela.net.chat.register.protocol;

import accela.net.chat.common.MemberID;
import accela.net.chat.register.Register;
import accela.net.chat.register.RegisterMessage;

public class KeepMeRegistered extends RegisterMessage<String, Boolean>
{
	private static final long serialVersionUID = 1L;

	public KeepMeRegistered()
	{
		super(null);
	}

	@Override
	protected Boolean handleImpl(MemberID requesterID, Register register)
	{
		return register.touch(requesterID);
	}
}
