package weibo4j.examples;


import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * @author Reilost
 */
public class Test {

    protected void printStatus(Status status, StringBuffer sb) {
        boolean isRT = false;
        if (status.getRetweeted_status() != null) {
            isRT = true;
        }
        sb.append(isRT ? "转发微博" : "原创微博").append(": ")
                .append(status.getUser().getScreenName()).append(" 在 ")
                .append(status.getCreatedAt()).append("时说:")
                .append(status.getText());
        if (isRT) {
            sb.append(" 转发原文 ").append(status.getRetweeted_status().getUser().getScreenName())
                    .append(" 说:").append(status.getRetweeted_status().getText());
        }
    }

    private void run() {
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        OAuthAPIExamples oauthAPI = new OAuthAPIExamples();
        //Weibo user = oauthAPI.getWeiboWithToken("d9ddc51b9f84f211206eb4124a74601b", "35d1ff8d00d9093a666fbc705acc8629");
        Weibo user = oauthAPI.doOAuth();

        Status updateStatus = null;
        try {
            updateStatus = user.updateStatus("进行update测试.....@Reilost "
                    + System.currentTimeMillis(), 20, 10);
            System.out.println("=======updateStatus=======");
            StringBuffer sb = new StringBuffer();
            printStatus(updateStatus, sb);
            System.out.println(sb);
        } catch (WeiboException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Test t = new Test();
        t.run();


//	/**
//	 * 执行timelineapi下全部接口示例
//	 */
//	TimeLineAPIExamples statusAPI= new TimeLineAPIExamples();
//	statusAPI.runAllAPI(userId);
        /**
         * 执行微博访问接口下的全部接口示例
         */
//        WeiboViewAPIExamples weiboViewAPI = new WeiboViewAPIExamples();
//        weiboViewAPI.runAllAPI(userId);
//    /**
//     * 执行用户接口下的全部接口示例
//     */
//    UserAPIExamples userAPI= new UserAPIExamples();
//    userAPI.runAllAPI(userId);
//	/**
//	 * 执行私信接口下的全部接口示例
//	 */
//	DirectMessageAPIExample dmAPI= new DirectMessageAPIExample();
//	dmAPI.setUserA(userA);
//	dmAPI.runAllAPI(userId);
//	/**
//	 * 执行关注接口下的全部接口示例
//	 */
//	FriendShipsAPIExamples firendShipAPI= new FriendShipsAPIExamples();
//	firendShipAPI.runAllAPI(userId);
//	/**
//	 * 执行话题接口下的全部接口示例
//	 */
//	TrendsAPIExamples trendsAPI=new TrendsAPIExamples();
//	trendsAPI.runAllAPI(userId);
//	/**
//	 *  执行Social Graph下的全部接口示例
//	 */
//	SocialGraphAPIExamples socialAPI= new SocialGraphAPIExamples();
//	socialAPI.runAllAPI(userId);
//	/**
//	 * 执行黑名单下的全部接口示例
//	 */
//	BlocksAPIExamples blocksAPI= new BlocksAPIExamples();
//	blocksAPI.runAllAPI(userId);
//	/**
//	 * 执行标签下的全部接口示例
//	 */
//	TagsAPIExamples tagsAPI= new TagsAPIExamples();
//	tagsAPI.runAllAPI(userId);
//
//	/**
//	 * 执行搜索下的接口示例
//	 */
//	SearchAPIExamples searchAPI= new SearchAPIExamples();
//	searchAPI.runAllAPI(userId);
    }


}
