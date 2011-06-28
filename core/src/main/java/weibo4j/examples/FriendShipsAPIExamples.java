package weibo4j.examples;

import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * 关注接口示例
 * @author Reilost
 *
 */
public class FriendShipsAPIExamples extends BaseExamples implements
		BaseInterface {

	@Override
	public void runAllAPI(Weibo wb) {
		this.weibo=wb;
		try {
			User user= weibo.showUser("reilost");
			boolean oldShip=weibo.existsFriendship( Long.toString(weibo.verifyCredentials().getId()), Long.toString(user.getId()));
			System.out.println("当前用户与测试用户的关注关系:"+(oldShip?"存在":"不存在"));
			if(oldShip){
				System.out.println("取消关注");
				weibo.destroyFriendshipByUserid(Long.toString(user.getId()));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("添加关注");
			weibo.createFriendshipByUserid("" + user.getId());
			boolean isExists=weibo.existsFriendship( Long.toString(weibo.verifyCredentials().getId()), Long.toString(user.getId()));
			System.out.println("关注关系:"+(isExists?"存在":"不存在"));
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
