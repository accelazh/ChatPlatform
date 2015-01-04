package accela.net.chat.register;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import accela.net.chat.common.IntervalTimer;
import accela.net.chat.common.MemberID;

//成员名单登记薄，自动删除timeout的MemberID
public class Register
{
	public static final long TIME_OUT = 30000;

	private Map<MemberID, Long> members = new LinkedHashMap<MemberID, Long>();

	private IntervalTimer timer = new IntervalTimer(TIME_OUT);

	public Register()
	{
		// do nothing
	}

	private void checkTimeOut()
	{
		if (!timer.beep())
		{
			return;
		}

		// System.out.println("Register check time out");

		long curTime = System.currentTimeMillis();
		Iterator<Map.Entry<MemberID, Long>> itr = members.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry<MemberID, Long> e = itr.next();
			if (curTime - e.getValue() >= TIME_OUT)
			{
				itr.remove();
				// System.out.println("Register member timeout: " + e.getKey());
			}
			else
			{
				break;
			}
		}

	}

	public synchronized boolean touch(MemberID member)
	{
		if (null == member)
		{
			throw new NullPointerException("member is null");
		}
		if (!members.containsKey(member))
		{
			return false;
		}

		members.put(member, System.currentTimeMillis());
		return true;
	}

	public synchronized boolean add(MemberID member)
	{
		if (null == member)
		{
			throw new NullPointerException("member is null");
		}

		Long old = members.put(member, System.currentTimeMillis());
		checkTimeOut();
		return null == old;
	}

	public synchronized boolean contains(MemberID member)
	{
		if (null == member)
		{
			throw new NullPointerException("member is null");
		}

		boolean ret = members.containsKey(member);
		if (ret)
		{
			touch(member);
		}
		checkTimeOut();
		return ret;
	}

	public synchronized boolean remove(MemberID member)
	{
		if (null == member)
		{
			throw new NullPointerException("member is null");
		}

		checkTimeOut();
		return members.remove(member) != null;
	}

	public synchronized int size()
	{
		checkTimeOut();
		return members.size();
	}

	public synchronized Set<MemberID> members()
	{
		checkTimeOut();
		return new HashSet<MemberID>(members.keySet());
	}

	@Override
	public synchronized String toString()
	{
		return "Register [members=" + members + "]";
	}
}
