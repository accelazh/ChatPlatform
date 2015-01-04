package accela.net.chat;

import java.io.IOException;

import accela.net.chat.broadcast.Broadcaster;
import accela.net.chat.chatter.Chatter;
import accela.net.chat.common.MemberID;
import accela.net.chat.register.RegisterServer;

/* �½������ҵĲ��裺
 * 1. �½�һ��RegisterServer������һ���������������Ͻ������������ҳ�Ա�ĵǼ�
 * 2. �½�Chatter�����������ҳ�Ա���½�Chatterʱ����Ҫָ�����������õ�RegisterServer��
 *    Chatter���Զ����Լ�ע�ᵽRegisterServer�ϡ���Ҫ֪��ͬһ��������������Chatterʱ��
 *    ���Ե���Chatter.members()��������ȡ��RegisterServer���Ѿ��Ǽǵ�����Chatter������
 * 3. Chatter֮�以�෢����Ϣ
 * 4. Chatter.close()�����뿪�����ң����ر�Chatter��һ��Chatter�������������У�ֻ����
 *    һ�������ң���һ��RegisterServer�󶨡����Chatter���ܸ��������ң������½�һ��Chatter
 *    ����
 *    
 * Note:
 * 1. RegisterServer��Chatter��Broadcaster��Ҫ�ȵ���start()�������򿪺󣬲��ܹ�ʹ�á�
 *    ����󣬵���close()�����رա�
 * 2. Broadcaster��ȫ�ǿ�ѡ�ģ���������û����ϡ������ʹ������ͬһ�����ڷ��ͺͽ��չ㲥��
 * 3. MemberID�����ڱ�ʶ����ڵ���ݵġ�ͨ�������������ֲ�ͬ������ڵ㣬Ҳ��������Ӧ�ڵ�
 *    �������ӡ������ҵĸ�����Ա֮�䣬ʹ��MemberID��ʶ��ݡ�RegisterServerҲʹ��MemberID��ʶ
 *    �Լ�����ݣ����������������ҳ�Ա��ͨ��RegisterServer��MemberID�����Խ�����֮�����ӡ�
 */
public class ChatterFactory
{
	private ChatterFactory()
	{
		// do nothing
	}

	public static RegisterServer createRegisterServer() throws IOException
	{
		return new RegisterServer(0);
	}

	public static Chatter createChatter(MemberID registerServerID,
			Chatter.Ear ear) throws IOException
	{
		return new Chatter(0, registerServerID, ear);
	}

	public static Broadcaster createBroadcaster(Broadcaster.Ear ear)
			throws IOException
	{
		final int port = 8172;
		return new Broadcaster(port, ear);
	}

}
