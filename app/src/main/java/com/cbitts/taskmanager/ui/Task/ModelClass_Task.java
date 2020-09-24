package com.cbitts.taskmanager.ui.Task;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

public class ModelClass_Task implements Serializable {
    private String Title;
    private String Description;
    private String Date;
    private String Priority;
    private String Assigned_id;
    private String Assigned_name;
    private String Created_id;
    private String Created_name;
    private String Status;
    private String TaskId;
    private String Description_work;
    private String Image;
    private String flag;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    final String TAG = "ModelClass_task";

    public void setDescription_work(String description_work) {
        Description_work = description_work;
    }

    public String getDescription_work() {
        return Description_work;
    }

    public void addDescription(String temp){
        if (!TextUtils.isEmpty(Description_work))
        Description_work = (Description_work + "\n"+temp);
        else
            Description_work = temp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getTaskId() {
        return TaskId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ModelClass_Task(String title, String description, String date, String priority, String status, String taskId, String assigned_id, String assigned_name, String created_id, String created_name, String description_work, String flag) {
        Title = title;
        Description = description;
        Date = date;
        Priority = priority;
        Status = status;
        TaskId = taskId;
        Assigned_id = assigned_id;
        Assigned_name = assigned_name;
        Created_id = created_id;
        Created_name = created_name;
        Description_work = description_work;
        this.flag = flag;
        Log.d(TAG, "Value of description is " + Description_work);
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public void setAssigned_id(String assigned_id) {
        Assigned_id = assigned_id;
    }

    public void setAssigned_name(String assigned_name) {
        Assigned_name = assigned_name;
    }

    public void setCreated_id(String created_id) {
        Created_id = created_id;
    }

    public void setCreated_name(String created_name) {
        Created_name = created_name;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getDate() {
        return Date;
    }

    public String getPriority() {
        return Priority;
    }

    public String getAssigned_id() {
        return Assigned_id;
    }

    public String getAssigned_name() {
        return Assigned_name;
    }

    public String getCreated_id() {
        return Created_id;
    }

    public String getCreated_name() {
        return Created_name;
    }
}
