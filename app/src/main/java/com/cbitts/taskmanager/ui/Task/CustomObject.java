package com.cbitts.taskmanager.ui.Task;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CustomObject {

    RecyclerView recyclerView;
    Context context;
    Activity activity;
    TextView loading;
    int holder_edit, filter;

    public CustomObject(RecyclerView recyclerView, Context context, Activity activity, TextView loading, int holder_edit, int filter) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.activity = activity;
        this.loading = loading;
        this.holder_edit = holder_edit;
        this.filter = filter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public TextView getLoading() {
        return loading;
    }

    public void setLoading(TextView loading) {
        this.loading = loading;
    }

    public int getHolder_edit() {
        return holder_edit;
    }

    public void setHolder_edit(int holder_edit) {
        this.holder_edit = holder_edit;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }
}
