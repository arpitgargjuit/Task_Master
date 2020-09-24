package com.cbitts.taskmanager.ui.Task;

import androidx.recyclerview.widget.RecyclerView;

public class Add_task_generator {
    String uid="";
    String name = "";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    RecyclerView.ViewHolder x;

    public RecyclerView.ViewHolder getX() {
        return x;
    }

    public void setX(RecyclerView.ViewHolder x) {
        this.x = x;
    }

    public Add_task_generator() {
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
