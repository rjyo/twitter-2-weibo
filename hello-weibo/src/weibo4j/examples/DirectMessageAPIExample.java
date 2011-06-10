package weibo4j.examples;

import java.util.List;

import weibo4j.DirectMessage;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * 私信接口示例
 * @author Reilost
 *
 */
public class DirectMessageAPIExample extends BaseExamples implements
		BaseInterface {
	private Weibo userA;
	
	@Override
	public void runAllAPI(Weibo wb) {
		this.weibo=wb;
		paging.setCount(20);
		paging.setPage(1);
		try {
			DirectMessage sendByUserid=direct_messages_new(userA.verifyCredentials().getId(), "send dm,你懂的..."+System.currentTimeMillis());
			DirectMessage sendByScreenName=direct_messages_new(userA.verifyCredentials().getScreenName(), "send dm,你懂的..."+System.currentTimeMillis());
			DirectMessage sendByScreenName1=direct_messages_new(userA.verifyCredentials().getScreenName(), "send dm,你懂的..."+System.currentTimeMillis());
			direct_messages();
			direct_messages_sent();
			direct_messages_destroy(sendByUserid.getId());
			direct_messages_destroy_batch(sendByScreenName.getId(),sendByScreenName1.getId());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 批量删除私信
	 * @param ids
	 * @throws WeiboException
	 */
	private void direct_messages_destroy_batch(long ...ids)  throws WeiboException {
		StringBuffer sb= new StringBuffer();
		for(long id:ids){
			sb.append(id).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		List<DirectMessage> destroyDMbatch=weibo.destroyDirectMessages(sb.toString());
		System.out.println("=======destroyDMbatch=======");
		printList(destroyDMbatch);
	}
	/**
	 * 删除一条私信
	 * @param id
	 * @throws WeiboException
	 */
	private void direct_messages_destroy(long id)  throws WeiboException {
		DirectMessage destroyDM=weibo.destroyDirectMessage(id);
		System.out.println("=======destroyDM=======");
		printWeiboObj(destroyDM);
	}
	/**
	 * 根据用户id发送一条私信
	 * @param id
	 * @param text
	 * @return
	 * @throws WeiboException
	 */
	private DirectMessage direct_messages_new(long id,String text) throws WeiboException {
		DirectMessage sendDMByuserID=weibo.sendDirectMessage(""+id ,text);
		System.out.println("=======sendDMByuserID=======");
		printWeiboObj(sendDMByuserID);
		return sendDMByuserID;
	}
	/**
	 * 根据用户昵称发送一条私信
	 * @param screenName
	 * @param text
	 * @return
	 * @throws WeiboException
	 */
	private DirectMessage direct_messages_new(String screenName,String text) throws WeiboException {
		DirectMessage sendDM=weibo.sendDirectMessageByScreenName(screenName ,text);
		System.out.println("=======sendDMByscreenName=======");
		printWeiboObj(sendDM);
		return sendDM;
	}
	/**
	 * 获取当前用户发送的最新私信列表
	 * @throws WeiboException
	 */
	private void direct_messages_sent() throws WeiboException{
		List<DirectMessage> sentDirectMessages=weibo.getSentDirectMessages(paging);
		System.out.println("=======sentDirectMessages=======");
		printList(sentDirectMessages);
	}
	/**
	 * 获取当前用户最新私信列表
	 * @throws WeiboException
	 */
	private void direct_messages() throws WeiboException{
		List<DirectMessage> dms=userA.getDirectMessages(paging);
		System.out.println("=======direct_messages=======");
		printList(dms);
		
	}
	public void setUserA(Weibo user) {
		this.userA = user;
	}

}
