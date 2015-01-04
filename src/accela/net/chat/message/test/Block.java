package accela.net.chat.message.test;

import java.io.Serializable;

public class Block implements Serializable
{
	private static final long serialVersionUID = 1L;

	public int a = 10;
	
	public String s = "hello";
	
	public Block()
	{
		//do nothing
	}
	
	public Block(int a, String s)
	{
		super();
		this.a = a;
		this.s = s;
	}
}
