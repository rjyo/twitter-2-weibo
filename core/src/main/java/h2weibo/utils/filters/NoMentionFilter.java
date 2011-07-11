package h2weibo.utils.filters;

import org.apache.log4j.Logger;

public class NoMentionFilter implements StatusFilter {
    private static final Logger log = Logger.getLogger(NoMentionFilter.class.getName());

    public String filter(String input) {
        input = input.replaceAll("\\n", "");
        input = input.replaceAll("\\r", "");

        if (input.matches(".*@\\w+.*")) {
            log.info("Dropped " + input + " with @somebody");
            return null; // if has @someone, skip
        } else {
            return input; // if has @someone, skip
        }
    }
}
