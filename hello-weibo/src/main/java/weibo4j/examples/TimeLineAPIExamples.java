package weibo4j.examples;

import java.util.List;

import weibo4j.Comment;
import weibo4j.Count;
import weibo4j.Emotion;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * 获取下行数据集(timeline)接口示例
 * 
 * @author Reilost
 * 
 */
public class TimeLineAPIExamples extends BaseExamples implements BaseInterface {
   
    public void runAllAPI(Weibo wb) {
	weibo = wb;
	paging.setCount(20);
	paging.setPage(1);
	try {
	    statuses_public_timeline();
	    statuses_friends_timeline();
	    statuses_user_timeline_myself();
	    statuses_user_timeline_someuser();
	    statuses_mentions();
	    statuses_comments_timeline();
	    statuses_comments_by_me();
	    statuses_comments_to_me();
	    statuses_comments();
	    statuses_counts();
	    statuses_repost_timeline();
	    statuses_repost_by_me();
	    statuses_unread();
	    statuses_reset_count();
	    emotions();
	} catch (WeiboException e) {
	    e.printStackTrace();
	}
    }

    /**
     * 表情接口，获取表情列表
     */
    private void emotions() throws WeiboException {
	List<Emotion> emotions = weibo.getEmotions();
	System.out.println(emotions);
    }

    /**
     * 未读消息数清零接口
     */
    private void statuses_reset_count() throws WeiboException {
	System.out.println(weibo.resetCount(1));
    }

    /**
     * 获取当前用户未读消息数
     */
    private void statuses_unread() throws WeiboException {
	Count unread = weibo.getUnread();
	System.out.println("=======unread=======");
	System.out.println(unread);
    }

    /**
     * 返回用户转发的最新n条微博信息
     */
    private void statuses_repost_by_me() throws WeiboException {

	List<Status> repost = weibo.getrepostbyme("1899168757", paging);
	System.out.println("=======repost=======");
	printList(repost);
    }

    /**
     * 返回一条原创微博的最新n条转发微博信息
     */
    private void statuses_repost_timeline() throws WeiboException {
	List<Status> repostTimeline = weibo.getreposttimeline("6817717409",
		paging);
	System.out.println("=======repostTimeline=======");
	printList(repostTimeline);
    }

    /**
     * 批量获取一组微博的评论数及转发数
     */
    private void statuses_counts() throws WeiboException {
	List<Count> counts = weibo.getCounts("6817717409");
	System.out.println(counts);
    }

    /**
     * 根据微博消息ID返回某条微博消息的评论列表
     */
    private void statuses_comments() throws WeiboException {
	List<Comment> comments = weibo.getComments("6817717409", paging);
	System.out.println("=======comments=======");
	printList(comments);
    }

    /**
     * 获取当前用户收到的评论
     */
    private void statuses_comments_to_me() throws WeiboException {
	List<Comment> commentToMe = weibo.getCommentsToMe(paging);
	System.out.println("=======commentToMe=======");
	printList(commentToMe);
    }

    /**
     * 获取当前用户发出的评论
     */
    private void statuses_comments_by_me() throws WeiboException {
	List<Comment> commentByMe = weibo.getCommentsByMe(paging);
	System.out.println("=======commentByMe=======");
	printList(commentByMe);
    }

    /**
     * 获取当前用户发送及收到的评论列表
     */
    private void statuses_comments_timeline() throws WeiboException {
	List<Comment> commentsTimeline = weibo.getCommentsTimeline(paging);
	System.out.println("=======commentsTimeline=======");
	printList(commentsTimeline);
    }

    /**
     * 获取@当前用户的微博列表
     */
    private void statuses_mentions() throws WeiboException {
	List<Status> myMentions = weibo.getMentions(paging);
	System.out.println("=======myMentions=======");
	printList(myMentions);
    }

    /**
     * 获取指定用户发布的微博列表
     */
    private void statuses_user_timeline_someuser() throws WeiboException {
	List<Status> someUserTimeLine = weibo
		.getUserTimeline("Reilost", paging);
	System.out.println("=======someUserTimeLine=======");
	printList(someUserTimeLine);
    }

    /**
     * 获取当前登录用户发布的微博消息列表
     */

    private void statuses_user_timeline_myself() throws WeiboException {
	List<Status> userTimeLine = weibo.getUserTimeline(paging);
	System.out.println("=======userTimeLine=======");
	printList(userTimeLine);
    }

    /**
     * 获取当前登录用户及其所关注用户的最新微博消息
     */
    private void statuses_friends_timeline() throws WeiboException {
	List<Status> friendsTimeLine = weibo.getFriendsTimeline(paging);
	System.out.println("=======friendsTimeLine=======");
	printList(friendsTimeLine);
    }

    /**
     * 获取最新的公共微博消息
     */
    private void statuses_public_timeline() throws WeiboException {
	List<Status> publicTimeLine = weibo.getPublicTimeline(30, 0);
	System.out.println("=======publicTimeline=======");
	printList(publicTimeLine);
    }

   

}
