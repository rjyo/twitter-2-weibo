package weibo4j.examples;

import java.util.List;

import weibo4j.Tag;
import weibo4j.Weibo;
import weibo4j.WeiboException;

/**
 * @author Reilost
 *
 */
public class TagsAPIExamples extends BaseExamples implements BaseInterface {

	@Override
	public void runAllAPI(Weibo wb) {
		this.weibo=wb;
		try {
			paging.setCount(20);
			/**
			 * 获取用户标签
			 */
//			List<Tag> tags=weibo.getTags("1899168757",paging);
//			printList(tags);
			/**
			 * 新建标签
			 */
			List<Tag> createdTags =weibo.createTags("测试","继续测试","还是测试");
			printList(createdTags);
			/**
			 * 返回用户感兴趣的标签
			 */
			List<Tag> sggestionTags=weibo.getSuggestionsTags();
			printList(sggestionTags);
			
			/**
			 * 删除标签
			 */
			StringBuffer ids= new StringBuffer();
			for(int i=0;i<createdTags.size();i++){
				if(i==0){
					weibo.destoryTag(createdTags.get(0).getId());
				}else{
					ids.append(createdTags.get(i).getId()).append(",");
				}
			}
			ids.deleteCharAt(ids.length()-1);
			/**
			 * 批量删除标签
			 */
			weibo.destory_batchTags(ids.toString());
			
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
