package accela.net.chat.register.protocol;

import java.util.Set;

import accela.net.chat.common.MemberID;
import accela.net.chat.register.Register;
import accela.net.chat.register.RegisterMessage;

public class WhoAreRegistered extends RegisterMessage<String, Set<MemberID>>
{
	private static final long serialVersionUID = 1L;

	public WhoAreRegistered()
	{
		super(null);
	}

	@Override
	protected Set<MemberID> handleImpl(MemberID requesterID, Register register)
	{
		return register.members();
	}

}
