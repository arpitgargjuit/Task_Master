package com.cbitts.taskmanager.ui.Report;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.ui.Task.CustomObject;
import com.cbitts.taskmanager.ui.Task.ModelClass_Task;
import com.cbitts.taskmanager.ui.Task.recyclerView_adapter_tasks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Waiting_report_dataSetter {
    RecyclerView recyclerView;
    Context context;
    Activity activity;
    List<ModelClass_Task> task_list = new ArrayList<>();
    String name;
    String uid;
    TextView loading;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    int holder_edit, filter;
    CustomObject object;
    Uri temp_imageUri;

    public Waiting_report_dataSetter(Context context, RecyclerView recyclerView, Activity activity, TextView loading, int holder_edit, int filter) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.activity = activity;
        this.loading = loading;
        this.holder_edit = holder_edit;
        this.filter = filter;
        object = new CustomObject(recyclerView,context,activity,loading,holder_edit,filter);
    }
    public void getdata(){
        loading.setVisibility(View.VISIBLE);
        loading.setText("Loading...");
        SharedPreferences getshared = activity.getSharedPreferences("user_details",Context.MODE_PRIVATE);
        uid = getshared.getString("uid","null");
        name = getshared.getString("name","null");
        getdata1();
    }

    private void getdata1() {
        if (uid.equals("null")){
            uid = firebaseAuth.getCurrentUser().getUid();
            getdata1();
        }
        if (name.equals("null")){
            firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    name = documentSnapshot.getString("name");
                }
            });
            getdata1();
        }
        else {
            recyclerView.setVisibility(View.GONE);
            task_list.clear();

            if (filter == 0){
                getassignedtask();
                getcreatedtask();
            }
            else if (filter == 1){
                getassignedtask();
            }
            else if (filter == 2){
                getcreatedtask();
            }
        }
    }

    private void getassignedtask() {
        firebaseFirestore.collection("tasks").whereEqualTo("created_by_uid",uid).orderBy("timestamp_1", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String temp_title, temp_description, temp_date, temp_priority, temp_assignid, temp_assignname, temp_status, temp_taskid, temp_work_description, temp_createDate, temp_flag;
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            temp_title = documentSnapshot.getString("title");
                            temp_description = documentSnapshot.getString("description");
                            temp_date = documentSnapshot.getString("due_date");
                            temp_createDate = documentSnapshot.getString("create_date");
                            temp_priority = documentSnapshot.getString("priority");
                            temp_assignid = documentSnapshot.getString("assigned_to");
                            temp_assignname = documentSnapshot.getString("assigned_to_name");
                            temp_status = documentSnapshot.getString("status");
                            temp_taskid = documentSnapshot.getString("task_id");
                            temp_flag = documentSnapshot.getString("image");
                            temp_work_description = documentSnapshot.getString("description_work");
                            if (temp_status.equals("Waiting confirmation") || temp_status.equals("Waiting acceptance")){
                                final ModelClass_Task modelClass_task = new ModelClass_Task(temp_title, temp_description, temp_date, temp_priority, temp_status, temp_taskid, temp_assignid, temp_assignname, uid, name, temp_work_description, temp_flag, temp_createDate);
                                task_list.add(modelClass_task);
                                if (temp_flag.equals("1")) {

                                    FirebaseStorage.getInstance().getReference().child("document/*" + temp_taskid).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    temp_imageUri = uri;
                                                    Log.d("image_get", uri + "");
                                                    modelClass_task.setImage(temp_imageUri);

                                                    //todo store the retrieved image in object ang pass to modelclass.setimage

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("image_error", e.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView_adapter_tasks adapter = new recyclerView_adapter_tasks(context, task_list,holder_edit,object);
                        recyclerView.setAdapter(adapter);
                        if (task_list.size()==0){
                            loading.setText("No Waiting Tasks");
                        }
                        else {
                            loading.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
                loading.setText("Unable to Load");
            }
        });
    }

    private void getcreatedtask(){
        firebaseFirestore.collection("tasks").whereEqualTo("assigned_to",uid).orderBy("timestamp_1", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String temp_title, temp_description, temp_date, temp_priority, temp_creatorid, temp_creatorname, temp_status, temp_taskid, temp_work_description, temp_createDate, temp_flag;
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            temp_title = documentSnapshot.getString("title");
                            temp_description = documentSnapshot.getString("description");
                            temp_date = documentSnapshot.getString("due_date");
                            temp_createDate = documentSnapshot.getString("create_date");
                            temp_priority = documentSnapshot.getString("priority");
                            temp_creatorid = documentSnapshot.getString("created_by_uid");
                            temp_creatorname = documentSnapshot.getString("created_by_name");
                            temp_status = documentSnapshot.getString("status");
                            temp_taskid = documentSnapshot.getString("task_id");
                            temp_flag = documentSnapshot.getString("image");
                            temp_work_description = documentSnapshot.getString("description_work");
                            if (temp_status.equals("Waiting confirmation") || temp_status.equals("Waiting acceptance")) {
                                final ModelClass_Task modelClass_task = new ModelClass_Task(temp_title, temp_description, temp_date, temp_priority, temp_status, temp_taskid, uid, name, temp_creatorid, temp_creatorname, temp_work_description, temp_flag, temp_createDate);
                                task_list.add(modelClass_task);
                                if (temp_flag.equals("1")) {

                                    FirebaseStorage.getInstance().getReference().child("document/*" + temp_taskid).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    temp_imageUri = uri;
                                                    Log.d("image_get", uri + "");
                                                    modelClass_task.setImage(temp_imageUri);

                                                    //todo store the retrieved image in object ang pass to modelclass.setimage

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("image_error", e.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView_adapter_tasks adapter = new recyclerView_adapter_tasks(context, task_list,holder_edit,object);
                        recyclerView.setAdapter(adapter);
                        if (task_list.size()==0){
                            loading.setText("No Waiting Tasks");
                        }
                        else {
                            loading.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
                loading.setText("Unable to Load");
            }
        });
    }

}
