package weibo4j.examples;

import java.util.List;

import weibo4j.IDs;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * 黑名单接口示例
 * 
 * @author Reilost
 * 
 */
public class BlocksAPIExamples extends BaseExamples implements BaseInterface {

	public void runAllAPI(Weibo wb) {
		this.weibo = wb;
		try {
			boolean isExists = weibo.existsBlock("1899168757");
			System.out.println("黑名单用户1899168757" + (isExists ? "存在" : "不存在"));
			if (!isExists) {
				System.out.println("加入黑名单");
				weibo.createBlock("1899168757");
			}
			System.out.println("取消黑名单");
			weibo.destroyBlock("1899168757");
			// 列出黑名单用户(输出用户详细信息)
			List<User> blockUsers = weibo.getBlockingUsers();
			System.out.println("=========blocks/blocking ============");
			printList(blockUsers);
			//列出分页黑名单用户（只输出id）
			System.out.println("=========blocks/blocking/ids ============");
			IDs ids = weibo.getBlockingUsersIDs();
			for (long id : ids.getIDs()) {
				System.out.println(id);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
