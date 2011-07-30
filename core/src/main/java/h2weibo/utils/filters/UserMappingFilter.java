package h2weibo.utils.filters;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMappingFilter extends DBStatusFilter {
    public String filter(String input) {
        Pattern p = Pattern.compile("@(\\w+)");
        Matcher m = p.matcher(input);

        Map<String, String> userMap = getHelper().getUserMap();

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
