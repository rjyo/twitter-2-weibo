package weibo4j.examples;

import weibo4j.*;

import java.util.List;

/**
 * @author Reilost
 *
 */
public class BaseExamples {
    protected Paging paging = new Paging();
    protected Weibo weibo = null;
    protected  <T> void printList(List<T> list) {
	for(T obj:list) {
	    printWeiboObj(obj);
	}
    }
    protected  <T> void printWeiboObj(T obj) {
	StringBuffer stringBuffer= new StringBuffer();
	if(obj instanceof Status) {
	    printStatus((Status)obj,stringBuffer);
	}else if(obj instanceof Comment) {
	    printComme1nt((Comment) obj,stringBuffer);
	}else if(obj instanceof User) {
	    printUser((User) obj,stringBuffer);
	}else if(obj instanceof DirectMessage){
		printDM((DirectMessage)obj,stringBuffer);
	}else if (obj instanceof UserTrend){
		printUserTrend((UserTrend)obj,stringBuffer);
	}else if(obj instanceof Trends){
		printTrends((Trends)obj,stringBuffer);
	}else if(obj instanceof Tag){
		printTags((Tag) obj,stringBuffer);
	}
	System.out.println(stringBuffer.toString());
    }
    private void printTags(Tag tag, StringBuffer sb) {
    	sb.append("标签:").append(tag.getValue()).append(" tag id 是").append(tag.getId());
	}
	private void printTrends(Trends trends, StringBuffer sb) {
    	sb.append("话题有:");
    	for(Trend trend:trends.getTrends() ){
    		sb.append(trend.getName()).append("\n");
    	}
	}
	private void printUserTrend(UserTrend userTrend, StringBuffer sb) {

    		sb.append("话题id:").append(userTrend.getTrend_id())
    		.append("话题名称:").append(userTrend.getHotword());
	}
	private void printDM(DirectMessage dm, StringBuffer sb) {
    	sb.append(dm.getSenderScreenName()).append("发送私信给").append(dm.getRecipientScreenName())
    	.append("说:").append(dm.getText());
	}
	protected void printUser(User user, StringBuffer sb) {
	sb.append("用户信息:用户id:").append(user.getId())
	.append(" 昵称是:").append(user.getScreenName())
	.append(" 拥有粉丝:").append(user.getFollowersCount()).append("人")
	.append(" 粉了").append(user.getFriendsCount()).append("人");
	
    }
    protected  void printComme1nt(Comment comment,StringBuffer sb) {
	
	sb.append(comment.getUser().getScreenName()).append("说 ")
	.append(comment.getText());
    }

    protected  void printStatus(Status status,StringBuffer sb) {
	    boolean isRT=false;
	    if(status.getRetweeted_status()!=null) {
		isRT=true;
	    }
	    sb.append(isRT?"转发微博":"原创微博").append(": ")
	    .append(status.getUser().getScreenName()).append(" 在 ")
	    .append(status.getCreatedAt()).append("时说:")
	    .append(status.getText());
	    if(isRT) {
		sb.append(" 转发原文 ").append(status.getRetweeted_status().getUser().getScreenName())
		.append(" 说:").append(status.getRetweeted_status().getText());
	    }
    }
}
