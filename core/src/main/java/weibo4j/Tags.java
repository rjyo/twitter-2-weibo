package weibo4j;

import java.util.List;

import weibo4j.model.Paging;
import weibo4j.model.PostParameter;
import weibo4j.model.Tag;
import weibo4j.model.TagWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;
import weibo4j.util.WeiboConfig;

public class Tags {
	/*----------------------------标签接口----------------------------------------*/
	/**
	 * 返回指定用户的标签列表
	 * 
	 * @param uid
	 *            要获取的标签列表所属的用户ID
	 * @return list of the tags
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/tags">tags</a>
	 * @since JDK 1.5
	 */
	public List<Tag> getTags(String uid) throws WeiboException {
		return Tag.constructTags(Weibo.client.get(WeiboConfig.getValue("baseURL")
						+ "tags.json", new PostParameter[] { new PostParameter(
						"uid", uid) }));
	}

	/**
	 * 返回指定用户的标签列表
	 * 
	 * @param uid
	 *            要获取的标签列表所属的用户ID
	 * @param page
	 *            返回结果的页码，默认为1
	 * @return list of the tags
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/tags">tags</a>
	 * @since JDK 1.5
	 */
	public List<Tag> getTags(String uid, Paging page) throws WeiboException {
		return Tag
				.constructTags(Weibo.client.get(WeiboConfig.getValue("baseURL")
						+ "tags.json", new PostParameter[] { 
					new PostParameter("uid", uid) }, page));
	}

	/**
	 * 批量获取用户的标签列表
	 * 
	 * @param uids
	 *            要获取标签的用户ID。最大20，逗号分隔
	 * @return list of the tags
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/tags/tags_batch">tags/tags_batch</a>
	 * @since JDK 1.5
	 */
	public TagWapper getTagsBatch(String uids) throws WeiboException {
		return Tag.constructTagWapper(Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "tags/tags_batch.json",
				new PostParameter[] { new PostParameter("uids", uids) }));
	}

	/**
	 * 获取系统推荐的标签列表
	 * 
	 * @return list of the tags
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/tags/suggestions">tags/suggestions</a>
	 * @since JDK 1.5
	 */

	public List<Tag> getTagsSuggestions() throws WeiboException {
		return Tag.constructTags(Weibo.client.get(WeiboConfig
				.getValue("baseURL") + "tags/suggestions.json"));
	}

	/**
	 * 为当前登录用户添加新的用户标签
	 * 
	 * @param tags
	 *            要创建的一组标签，用半角逗号隔开，每个标签的长度不可超过7个汉字，14个半角字符
	 * @return tag_id
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/tags/create">tags/create</a>
	 * @since JDK 1.5
	 */
	public JSONArray createTags(String tags) throws WeiboException {
		return Weibo.client.post(WeiboConfig.getValue("baseURL") + "tags/create.json",
				new PostParameter[] { new PostParameter("tags", tags) }).asJSONArray();
	}

	/**
	 * 删除一个用户标签
	 * 
	 * @param tag_id
	 *            要删除的标签ID
	 * @return
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @throws JSONException
	 * @see <a href="http://open.weibo.com/wiki/2/tags/destroy">tags/destroy</a>
	 * @since JDK 1.5
	 */
	public JSONObject destoryTag(Integer tag_id) throws WeiboException {
			return Weibo.client.post(WeiboConfig.getValue("baseURL") + "tags/destroy.json",
							new PostParameter[] { new PostParameter("tag_id",
									tag_id.toString()) }).asJSONObject();
	}

	/**
	 * 批量删除一组标签
	 * 
	 * @param ids
	 *            要删除的一组标签ID，以半角逗号隔开，一次最多提交10个ID
	 * @return tag_id
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/tags/destroy_batch">tags/destroy_batch</a>
	 * @since JDK 1.5
	 */
	public List<Tag> destroyTagsBatch(String ids) throws WeiboException {
		return Tag.constructTags(Weibo.client.post(
				WeiboConfig.getValue("baseURL") + "tags/destroy_batch.json",
				new PostParameter[] { new PostParameter("ids", ids) }));
	}
}
