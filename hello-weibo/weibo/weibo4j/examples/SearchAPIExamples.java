package weibo4j.examples;

import java.util.List;

import weibo4j.Gender;
import weibo4j.Query;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * @author Reilost
 *
 */
public class SearchAPIExamples extends BaseExamples implements BaseInterface {

	public void runAllAPI(Weibo wb) {
		this.weibo=wb;
		Query userQuery= new Query();
		userQuery.setQ("微博");
		userQuery.setSnick(true);
		userQuery.setGender(Gender.MALE);
		userQuery.setSort(0);
		try {
			List<User> userSearch=weibo.searchUser(userQuery);
			printList(userSearch);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
