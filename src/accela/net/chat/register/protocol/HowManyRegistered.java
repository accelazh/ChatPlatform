package accela.net.chat.register.protocol;

import accela.net.chat.common.MemberID;
import accela.net.chat.register.Register;
import accela.net.chat.register.RegisterMessage;

public class HowManyRegistered extends RegisterMessage<String, Integer>
{
	private static final long serialVersionUID = 1L;

	public HowManyRegistered()
	{
		super(null);
	}

	@Override
	protected Integer handleImpl(MemberID requesterID, Register register)
	{
		return register.size();
	}

}
