package weibo4j.examples;

import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.AccessToken;
import weibo4j.http.RequestToken;
import weibo4j.util.BareBonesBrowserLaunch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 登录/OAuth接口示例
 * @author Reilost
 *
 */
public class OAuthAPIExamples extends BaseExamples {
    protected Weibo doOAuth() {
	Weibo weibo = new Weibo();
	RequestToken requestToken;
	try {
	    requestToken = weibo.getOAuthRequestToken();
	    BareBonesBrowserLaunch.openURL(requestToken.getAuthenticationURL());
	    BufferedReader in=new BufferedReader(
		     new InputStreamReader(System.in));
	    System.out.println("请输入显示的pin码：");
	    String pin=in.readLine();
	    AccessToken accessToken=requestToken.getAccessToken(pin.trim());
	    weibo.setToken(accessToken);
	    System.out.println("授权帐号的token是:"+accessToken.getToken());
	    System.out.println("授权帐号的tokensecret是:"+accessToken.getTokenSecret());
	    return weibo;
	} catch (WeiboException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
    
    protected Weibo getWeiboWithToken(String token,String tokenSecret) {
	Weibo weibo= new Weibo();
	weibo.setToken(token, tokenSecret);
	return weibo;
    }
    protected Weibo doXAuth(String username,String password) {
	Weibo weibo= new Weibo();
	AccessToken accessToken;
	try {
	    accessToken = weibo.getXAuthAccessToken(username, password);
	    weibo.setToken(accessToken);
	    return weibo;
	} catch (WeiboException e) {
	    e.printStackTrace();
	}
	
	return null;
    }
    protected Weibo doBasicAuth(String username,String password) {
	return new Weibo(username, password);
    }
}
