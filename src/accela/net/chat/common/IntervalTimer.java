package accela.net.chat.common;

public class IntervalTimer
{
	private long interval = 0;

	private long lastBeepTime = 0;

	public IntervalTimer(long interval)
	{
		if (interval < 0)
		{
			throw new IllegalArgumentException("interval<0");
		}

		this.interval = interval;
		this.lastBeepTime = System.currentTimeMillis();
	}

	public boolean beep()
	{
		long curTime = System.currentTimeMillis();
		if (curTime - lastBeepTime >= interval)
		{
			lastBeepTime = curTime;
			return true;
		} else
		{
			return false;
		}
	}

	public long getLastBeepTime()
	{
		return this.lastBeepTime;
	}
}
