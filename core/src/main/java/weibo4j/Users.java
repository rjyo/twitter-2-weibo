/*
 * (The MIT License)
 *
 * Copyright (c) 2012 Rakuraku Jyo <jyo.rakuraku@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package weibo4j;

import weibo4j.http.HttpClient;
import weibo4j.model.PostParameter;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.util.WeiboConfig;

public class Users {
	
	public static HttpClient client = new HttpClient();


	/*----------------------------用户接口----------------------------------------*/
	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param uid
	 *            需要查询的用户ID
	 * @return User
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/users/show">users/show</a>
	 * @since JDK 1.5
	 */
	public User showUserById(String uid) throws WeiboException {
		return new User(Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "users/show.json",
				new PostParameter[] { new PostParameter("uid", uid) })
				.asJSONObject());
	}

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param screen_name
	 *            用户昵称
	 * @return User
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/users/show">users/show</a>
	 * @since JDK 1.5
	 */
	public User showUserByScreenName(String screen_name) throws WeiboException {
		return new User(Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "users/show.json",
				new PostParameter[] { new PostParameter("screen_name",
						screen_name) }).asJSONObject());
	}

	/**
	 * 通过个性化域名获取用户资料以及用户最新的一条微博
	 * 
	 * @param domain
	 *            需要查询的个性化域名。
	 * @return User
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/users/domain_show">users/domain_show</a>
	 * @since JDK 1.5
	 */
	public User showUserByDomain(String domain) throws WeiboException {
		return new User(Weibo.client.get(
				WeiboConfig.getValue("baseURL") + "users/domain_show.json",
				new PostParameter[] { new PostParameter("domain", domain) })
				.asJSONObject());
	}
	/**
	 * 批量获取用户的粉丝数、关注数、微博数
	 * 
	 * @param uids
	 *            需要获取数据的用户UID，多个之间用逗号分隔，最多不超过100个
	 * @return jsonobject
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/users/domain_show">users/domain_show</a>
	 * @since JDK 1.5
	 */
	public JSONArray getUserCount(String uids) throws WeiboException{
		return  Weibo.client.get(WeiboConfig.getValue("baseURL") + "users/counts.json",
				new PostParameter[] { new PostParameter("uids", uids)}).asJSONArray();
	}
}
