package weibo4j.examples;

import weibo4j.IDs;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * Social Graph接口示例
 * @author Reilost
 *
 */
public class SocialGraphAPIExamples extends BaseExamples implements
		BaseInterface {

	public void runAllAPI(Weibo wb) {
		this.weibo=wb;
		paging.setCount(20);
		paging.setPage(1);
		try {
			IDs ids=weibo.getFollowersIDSByScreenName("reilost", paging);
			System.out.println("==========getFollowersIDSByScreenName==========");
			printIDs(ids);
			ids=weibo.getFollowersIDSByUserId("1899168757", paging);
			System.out.println("==========getFollowersIDSByUserId==========");
			printIDs(ids);
			ids=weibo.getFriendsIDSByScreenName("reilost", paging);
			System.out.println("==========getFriendsIDSByScreenName==========");
			printIDs(ids);
			ids=weibo.getFriendsIDSByUserId("1899168757", paging);
			System.out.println("==========getFriendsIDSByUserId==========");
			printIDs(ids);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

	private void printIDs(IDs ids) {
		for(long id:ids.getIDs()){
			System.out.println(id);
		}
	}

}
