package h2weibo.utils.filters;

public class TagStatusFilter implements StatusFilter {

    public String filter(String input) {
        if (input == null) return input;
        return input.replaceAll("(#\\w+)", "$1#");
    }
}
