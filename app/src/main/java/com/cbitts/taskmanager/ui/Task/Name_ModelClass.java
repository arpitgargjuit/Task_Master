package com.cbitts.taskmanager.ui.Task;

public class Name_ModelClass {
    String Name,uid;

    public Name_ModelClass(String name, String uid) {
        Name = name;
        this.uid = uid;
    }

    public String getName() {
        return Name;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
