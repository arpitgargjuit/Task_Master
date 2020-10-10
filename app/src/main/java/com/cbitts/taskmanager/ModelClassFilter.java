package com.cbitts.taskmanager;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ModelClassFilter {
    public static int filter;
    SwipeRefreshLayout swipeRefreshLayout;

    public ModelClassFilter(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public static int getFilter() {
        return filter;
    }

    public static void setFilter(int filter) {
        ModelClassFilter.filter = filter;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }
}
