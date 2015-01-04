package accela.net.chat.register.protocol;

import accela.net.chat.common.MemberID;
import accela.net.chat.register.Register;
import accela.net.chat.register.RegisterMessage;

public class UnregisterMe extends RegisterMessage<String, Boolean>
{
	private static final long serialVersionUID = 1L;

	public UnregisterMe()
	{
		super(null);
	}

	@Override
	protected Boolean handleImpl(MemberID requesterID, Register register)
	{
		return register.remove(requesterID);
	}

}
