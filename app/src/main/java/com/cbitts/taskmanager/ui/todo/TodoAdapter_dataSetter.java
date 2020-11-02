package com.cbitts.taskmanager.ui.todo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.ui.Task.CustomObject;
import com.cbitts.taskmanager.ui.Task.ModelClass_Task;
import com.cbitts.taskmanager.ui.Task.recyclerView_adapter_tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TodoAdapter_dataSetter {

        RecyclerView recyclerView;
        Context context;
        Activity activity;
        List<ModelClass_Task> task_list = new ArrayList<>();
        String name;
        String uid;
        TextView loading;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        int holder_edit;
        CustomObject object;
        Uri temp_imageUri;
        public static int filter = 1;

        public TodoAdapter_dataSetter(Context context, RecyclerView recyclerView, Activity activity, TextView loading, int holder_edit) {
            this.recyclerView = recyclerView;
            this.context = context;
            this.activity = activity;
            this.loading = loading;
            this.holder_edit = holder_edit;
            object = new CustomObject(recyclerView, context, activity, loading, holder_edit);
        }

        public void getdata() {
            loading.setVisibility(View.VISIBLE);
            loading.setText("Loading...");
            SharedPreferences getshared = activity.getSharedPreferences("user_details", Context.MODE_PRIVATE);
            uid = getshared.getString("uid", "null");
            name = getshared.getString("name", "null");
            task_list.clear();

            if (!internetConnectionAvailable(10000)){
                loading.setText(R.string.internet_error);
                Toast.makeText(context, R.string.internet_error_toast, Toast.LENGTH_SHORT).show();
            }

            if (filter == 1 || filter == 2){
            getdata1();}
            if (filter == 1 || filter == 3 ){
            getdata2();}
        }


    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }

        private void getdata1() {
            firebaseFirestore.collection("users").document(uid).collection("self_task").orderBy("Date_due").orderBy("timestamp_created").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String temp_title, temp_description, temp_date, temp_priority, temp_assignid, temp_assignname, temp_status, temp_taskid, temp_work_description, temp_flag, temp_createDate;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                temp_title = documentSnapshot.getString("title");
                                temp_description = documentSnapshot.getString("description");
                                temp_date = documentSnapshot.getString("due_date");
                                temp_createDate = documentSnapshot.getString("create_date");
                                temp_priority = documentSnapshot.getString("priority");
                                temp_status = documentSnapshot.getString("status");
                                temp_taskid = documentSnapshot.getString("task_id");
                                temp_work_description = documentSnapshot.getString("description_work");
                                temp_flag = documentSnapshot.getString("image");

                                Calendar calendar = Calendar.getInstance();
                                java.util.Date c = calendar.getTime();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                try {
                                    Date due = df.parse(temp_date);
                                    calendar.setTime(due);
                                    calendar.add(Calendar.DATE, 1);
                                    due = calendar.getTime();
                                    if (!due.after(c)) {
                                        Log.d("check", "overdue");
                                        if (temp_status.equals("Pending")) {
                                            temp_status = "Overdue";
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.d("error_overdue", e.getMessage());
                                }

                                if (temp_status.equals("Pending")||temp_status.equals("Overdue")) {
                                    Log.d("taskSetter", temp_work_description + "size is " + task_list.size());
                                    final ModelClass_Task modelClass_task = new ModelClass_Task(temp_title, temp_description, temp_date, temp_priority, temp_status, temp_taskid, uid, name, temp_work_description, temp_flag, temp_createDate);
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
                            RecyclerView_adapter_todo adapter = new RecyclerView_adapter_todo(context, task_list, holder_edit, object);
                            recyclerView.setAdapter(adapter);
                            if (task_list.size() == 0) {
                                loading.setText("No Pending Tasks");
                            } else {
                                loading.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
                    Log.d("error", e.toString());
                    loading.setText("Unable to Load");
                }
            });
        }

    private void getdata2() {
        firebaseFirestore.collection("users").document(uid).collection("self_task").orderBy("timestamp_created").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String temp_title, temp_description, temp_date, temp_priority, temp_assignid, temp_assignname, temp_status, temp_taskid, temp_work_description, temp_flag, temp_createDate;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            temp_title = documentSnapshot.getString("title");
                            temp_description = documentSnapshot.getString("description");
                            temp_date = documentSnapshot.getString("due_date");
                            temp_createDate = documentSnapshot.getString("create_date");
                            temp_priority = documentSnapshot.getString("priority");
                            temp_status = documentSnapshot.getString("status");
                            temp_taskid = documentSnapshot.getString("task_id");
                            temp_work_description = documentSnapshot.getString("description_work");
                            temp_flag = documentSnapshot.getString("image");


                            if (temp_status.equals("Completed")) {
                                Log.d("taskSetter", temp_work_description + "size is " + task_list.size());
                                final ModelClass_Task modelClass_task = new ModelClass_Task(temp_title, temp_description, temp_date, temp_priority, temp_status, temp_taskid, uid, name, temp_work_description, temp_flag, temp_createDate);
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
                        RecyclerView_adapter_todo adapter = new RecyclerView_adapter_todo(context, task_list, holder_edit, object);
                        recyclerView.setAdapter(adapter);
                        if (task_list.size() == 0) {
                            loading.setText("No Pending Tasks");
                        } else {
                            loading.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went Wrong!!", Toast.LENGTH_SHORT).show();
                Log.d("error", e.toString());
                loading.setText("Unable to Load");
            }
        });
    }


    }

