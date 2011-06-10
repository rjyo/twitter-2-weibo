package weibo4j.examples;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import weibo4j.Comment;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.ImageItem;

import java.util.List;

/**
 * 微博访问接口示例
 * 
 * @author Reilost
 * 
 */
public class WeiboViewAPIExamples extends BaseExamples implements BaseInterface {

    public void runAllAPI(Weibo wb) {
	this.weibo = wb;
	try {
	    statuses_show();
	    Status updateStatus = statuses_update();
//	    Status uploadStatus = statuses_upload();
//	    Status repostStatus=statuses_repost();
//	    Comment comment=statuses_comment("6817717409");
//	    Comment comment1=statuses_comment("6817717409");
//	    Comment replyComment=statuses_reply("6817717409", comment.getId());
//	    statuses_destroy(uploadStatus);
//	    statuses_destroy(updateStatus);
//	    statuses_destroy(repostStatus);
//	    statuses_comment_destroy_id(comment);
//	    statuses_comment_destroy_batch(comment1,replyComment);
	  
	    
	} catch (WeiboException e) {
	    e.printStackTrace();
	}

    }
    /**
     * 批量删除评论
     * @param comments
     * @throws WeiboException
     */
    private void statuses_comment_destroy_batch(Comment...comments) throws WeiboException {
	StringBuffer sb= new StringBuffer();
	for(Comment comment:comments) {
	    if(comment!=null)
		sb.append(""+comment.getId()).append(",");
	}
	sb.deleteCharAt(sb.length()-1);
	List<Comment> destroyBatch=weibo.destroyComments(sb.toString());
	System.out.println("=======destroyBatch=======");
	printList(destroyBatch);
    }
    /**
     * 回复微博评论信息
     * @param statusId
     * @param commentId
     * @return
     * @throws WeiboException
     */
    private Comment statuses_reply(String statusId,Long commentId) throws WeiboException {
	Comment replyComment=weibo.reply(statusId,Long.toString(commentId),"对回复进行回复..你懂的"+System.currentTimeMillis());
	System.out.println("=======replyComment=======");
	printWeiboObj(replyComment);
	return replyComment;
    } 
    /**
     * 删除当前用户的微博评论信息
     * @param comment
     * @throws WeiboException
     */
    private void statuses_comment_destroy_id(Comment comment)
	    throws WeiboException {
	Comment destroyComment=weibo.destroyComment(comment.getId());
	System.out.println("=======destroyComment=======");
	printWeiboObj(destroyComment);
    }
    /**
     * 对一条微博信息进行评论
     * @return
     * @throws WeiboException
     */
    private Comment statuses_comment(String statusId) throws WeiboException {
	Comment comment=weibo.updateComment("发布评论,你懂的"+ System.currentTimeMillis(), statusId,null);
	System.out.println("=======comment=======");
	printWeiboObj(comment);
	return comment;
    }
    /**
     * 转发一条微博信息
     * @throws WeiboException
     */
    private Status statuses_repost() throws WeiboException {
	Status repostStauts=weibo.repost("6817717409", "repost.你懂的"+System.currentTimeMillis(), 0);
	System.out.println("=======repostStauts=======");
	printWeiboObj(repostStauts);
	return repostStauts;
    }

    /**
     * 删除一条微博信息
     * 
     * @param uploadStatus
     * @throws WeiboException
     */
    private void statuses_destroy(Status uploadStatus) throws WeiboException {
	Status destroyStatus = weibo.destroyStatus(uploadStatus.getId());
	System.out.println("=======destroyStatus=======");
	printWeiboObj(destroyStatus);
    }

    /**
     * 上传图片并发布一条微博信息
     * 
     * @return
     * @throws WeiboException
     */
    private Status statuses_upload() throws WeiboException {
	HttpClient client = new HttpClient();
	GetMethod getMethod = new GetMethod(
		"http://ww1.sinaimg.cn/thumbnail/713303f5jw6der25ngayig.gif");
	Status uploadStatus = null;
	try {
	    client.executeMethod(getMethod);
	    byte[] image = getMethod.getResponseBody();
	    ImageItem item = new ImageItem(image);
	    uploadStatus = weibo.uploadStatus("upload test.....@Reilost "
		    + System.currentTimeMillis(), item, -30, 40);
	    System.out.println("=======uploadStatus=======");
	    printWeiboObj(uploadStatus);
	    return uploadStatus;
	} catch (Exception e) {
	    throw new WeiboException(e);
	}
    }

    /**
     * 发布一条微博信息
     * 
     * @return
     * @throws WeiboException
     */
    private Status statuses_update() throws WeiboException {
	Status updateStatus = weibo.updateStatus("进行update测试.....@Reilost "
		+ System.currentTimeMillis(), 20, 10);
	System.out.println("=======updateStatus=======");
	printWeiboObj(updateStatus);
	return updateStatus;
    }

    /**
     * 根据ID获取单条微博信息内容
     * 
     * @throws WeiboException
     */
    private void statuses_show() throws WeiboException {
	Status showStatus = weibo.showStatus("6817717409");
	System.out.println("=======showStatus=======");
	printWeiboObj(showStatus);
    }

}
