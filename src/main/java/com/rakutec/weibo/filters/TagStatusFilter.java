package com.rakutec.weibo.filters;

public class TagStatusFilter implements StatusFilter {
    @Override
    public String filter(String input) {
        if (input == null) return input;
        return input.replaceAll("(#\\w+)", "$1#");
    }

    public static void main(String[] args) {
        TagStatusFilter tagStatusFilter = new TagStatusFilter();
        String s = tagStatusFilter.filter("asd2fhlad #af asdfhweal");
        System.out.println(s);
    }
}
