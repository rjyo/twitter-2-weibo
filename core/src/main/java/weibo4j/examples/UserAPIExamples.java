package weibo4j.examples;

import java.util.List;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * 用户接口示例
 * 
 * @author Reilost
 * 
 */
public class UserAPIExamples extends BaseExamples implements BaseInterface {
    public void runAllAPI(Weibo wb) {
	this.weibo = wb;
	paging.setCount(20);
	paging.setPage(1);
	try {

	    users_show("李开复");
	    users_show("1899168757");
	    statuses_friends("李开复");
	    statuses_friends("1899168757");
	    statuses_followers("李开复");
	    statuses_followers("1899168757");
	    users_hot("default");
	    user_friends_update_remark("1899168757","测试");//need to check
	    users_suggestions();
	} catch (WeiboException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    private void users_suggestions() throws WeiboException {
	List<User> suggestionsUser=weibo.getSuggestionUsers();
	System.out.println("=======suggestionsUser=======");
	printList(suggestionsUser);
    }
    /**
     * 更新当前登录用户所关注的某个好友的备注信息
     * @param userid
     * @param remark
     * @throws WeiboException
     */
    private void user_friends_update_remark(String userid,String remark) throws WeiboException {
	boolean isExists=weibo.existsFriendship(""+weibo.verifyCredentials().getId(), userid);
	if(!isExists) {
	    weibo.createFriendshipByUserid(userid);
	}
	User updateRemarUser=weibo.updateRemark(userid, remark);
	System.out.println("=======updateRemarUser=======");
	printWeiboObj(updateRemarUser);
    }

    /**
     * 获取系统推荐用户
     * 
     * @param id
     * @throws WeiboException
     */
    private void users_hot(String category) throws WeiboException {
	List<User> hotUsers = weibo.getHotUsers(category);
	System.out.println("=======hotUsers=======");
	printList(hotUsers);
    }
    /**
     * 获取用户粉丝列表及及每个粉丝用户最新一条微博
     * 
     * @param id
     * @throws WeiboException
     */
    private void statuses_followers(String id) throws WeiboException {
	List<User> followersStatus = weibo.getFollowersStatuses(id, paging);
	System.out.println("=======followersStatus=======");
	printList(followersStatus);
    }
    /**
     * 获取用户关注列表及每个关注用户最新一条微博
     * 
     * @param id
     * @throws WeiboException
     */
    private void statuses_friends(String id) throws WeiboException {
	List<User> friendsStatus = weibo.getFriendsStatuses(id, paging);
	System.out.println("=======friendsStatus=======");
	printList(friendsStatus);
    }

    /**
     * 根据用户ID获取用户资料
     * 
     * @param id
     * @throws WeiboException
     */
    private void users_show(String id) throws WeiboException {
	User showUser = weibo.showUser(id);
	System.out.println("=======showUser=======");
	printWeiboObj(showUser);
    }
}
