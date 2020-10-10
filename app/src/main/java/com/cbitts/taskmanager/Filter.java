package com.cbitts.taskmanager;

public class Filter {
    private static int filter=0;

    public Filter() {
    }

    public static int getFilter() {
        return filter;
    }

    public static void setFilter(int filter) {
        Filter.filter = filter;
    }
}
