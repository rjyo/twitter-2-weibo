package weibo4j.examples;

import java.util.List;

import weibo4j.Status;
import weibo4j.Trends;
import weibo4j.UserTrend;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * 话题接口示例
 * 
 * @author Reilost
 * 
 */
public class TrendsAPIExamples extends BaseExamples implements BaseInterface {

	public void runAllAPI(Weibo wb) {
		this.weibo = wb;
		paging.setCount(20);
		paging.setPage(1);
		try {
			List<UserTrend> userTrends = trends("1899168757");
			trends_statuses(userTrends);
			trends_destroy(userTrends);
			Thread.sleep(2000);
			trends_follow(userTrends);
			trends_hourly();
			trends_daily();
			trends_weekly();
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 按周返回热门话题
	 * 
	 * @throws WeiboException
	 */
	private void trends_weekly() throws WeiboException {
		List<Trends> trendsWeekly = weibo.getTrendsHourly(0);
		System.out.println("=======trendsWeekly=======");
		printList(trendsWeekly);
	}

	/**
	 * 按天返回热门话题
	 * 
	 * @throws WeiboException
	 */
	private void trends_daily() throws WeiboException {
		List<Trends> trendsDaily = weibo.getTrendsHourly(0);
		System.out.println("=======trendsDaily=======");
		printList(trendsDaily);
	}

	/**
	 * 按小时返回热门话题
	 * 
	 * @throws WeiboException
	 */
	private void trends_hourly() throws WeiboException {
		List<Trends> trendsHourly = weibo.getTrendsHourly(0);
		System.out.println("=======trendsHourly=======");
		printList(trendsHourly);
	}

	private void trends_destroy(List<UserTrend> userTrends)
			throws WeiboException {
		for (UserTrend trend : userTrends) {
			if (trend != null) {
				boolean res = weibo.trendsDestroy(trend.getTrend_id());
				System.out.println("取消关注话题" + trend.getHotword()
						+ (res ? "成功" : "失败"));
			}
		}
	}

	private void trends_follow(List<UserTrend> userTrends)
			throws WeiboException {
		for (UserTrend trend : userTrends) {
			if (trend != null) {
				weibo.trendsFollow(trend.getHotword());
				System.out.println("关注话题" + trend.getHotword());
			}
		}
	}

	/**
	 * 获取某一话题下的微博
	 * 
	 * @param userTrends
	 * @throws WeiboException
	 */
	private void trends_statuses(List<UserTrend> userTrends)
			throws WeiboException {
		for (UserTrend trend : userTrends) {
			if (trend != null) {
				List<Status> status = weibo.getTrendStatus(trend.getHotword(),
						paging);
				System.out.println("话题" + trend.getHotword() + "下的话题有:");
				printList(status);
			}
		}
	}

	/**
	 * 获取某人的话题
	 * 
	 * @throws WeiboException
	 */
	private List<UserTrend> trends(String userid) throws WeiboException {
		List<UserTrend> userTrends = weibo.getTrends(userid, paging);
		System.out.println("=======userTrends=======");
		printList(userTrends);
		return userTrends;
	}

}
