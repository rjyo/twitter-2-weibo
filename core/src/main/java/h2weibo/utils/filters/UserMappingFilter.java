package h2weibo.utils.filters;

import h2weibo.model.RedisHelper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMappingFilter implements StatusFilter {
    public String filter(String input) {
        Pattern p = Pattern.compile("@(\\w+)");
        Matcher m = p.matcher(input);

        RedisHelper helper = RedisHelper.getInstance();
        Map<String, String> userMap = helper.getUserMap();

        while (m.find()) {
            String userId = m.group(1);
            String weiboId = userMap.get(userId);

            if (weiboId != null) {
                input = input.replaceAll("@" + userId, "@" + weiboId);
            }
        }

        return input;
    }
}
