package com.cbitts.taskmanager.ui.Task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
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

public class Task_Adapter_dataSetter {
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

    public Task_Adapter_dataSetter(Context context, RecyclerView recyclerView, Activity activity, TextView loading, int holder_edit, int filter) {
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

    public boolean isConnected() throws InterruptedException, IOException {
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

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

        if (!internetConnectionAvailable(10000)){
            Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show();
            loading.setText(R.string.internet_error);
        }


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
    firebaseFirestore.collection("tasks").orderBy("timestamp_1", Query.Direction.DESCENDING).whereEqualTo("created_by_uid",uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String temp_title, temp_description, temp_date, temp_priority, temp_assignid, temp_assignname, temp_status, temp_taskid, temp_work_description,temp_flag, temp_createDate;
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            temp_title = documentSnapshot.getString("title");
                            temp_description = documentSnapshot.getString("description");
                            temp_date = documentSnapshot.getString("due_date");
                            temp_createDate = documentSnapshot.getString("create_date");
                            temp_priority = documentSnapshot.getString("priority");
                            temp_assignid = documentSnapshot.getString("assigned_to");
                            temp_assignname = documentSnapshot.getString("assigned_to_name");
                            temp_status = documentSnapshot.getString("status");
                            temp_taskid = documentSnapshot.getString("task_id");
                            temp_work_description = documentSnapshot.getString("description_work");
                            temp_flag = documentSnapshot.getString("image");

                            Calendar calendar =  Calendar.getInstance();
                            java.util.Date c = calendar.getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            try {
                                Date due = df.parse(temp_date);
                                calendar.setTime(due);
                                calendar.add(Calendar.DATE,1);
                                due = calendar.getTime();
                                if (due.after(c)){
                                    Log.d("check","pending");
                                    if (temp_status.equals("Overdue")){
                                        temp_status = "pending";
                                        HashMap<String, Object> over = new HashMap<>();
                                        over.put("status",temp_status);
                                        firebaseFirestore.collection("tasks").document(temp_taskid).update(over).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("status","Staus updated to overdue");
                                            }
                                        });
                                    }
                                }
                                else {
                                    Log.d("check","overdue");
                                    if (temp_status.equals("pending")) {
                                        temp_status = "Overdue";
                                        HashMap<String, Object> over = new HashMap<>();
                                        over.put("status", temp_status);
                                        firebaseFirestore.collection("tasks").document(temp_taskid).update(over).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("status", "Staus updated to overdue");
                                            }
                                        });
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d("error_overdue",e.getMessage());
                            }


//                            if (temp_flag.equals("1")){
//                                FirebaseStorage.getInstance().getReference().child("document/*"+temp_taskid).getDownloadUrl()
//                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                            @Override
//                                            public void onSuccess(Uri uri) {
//
//                                                temp_imageUri = uri;
//
//                                                //todo store the retrieved image in object ang pass to modelclass.setimage
//
//                                            }
//                                        });
//                            }

                            if (!temp_status.equals("Completed")&&!temp_status.startsWith("Deleted")&&(!temp_status.startsWith("Rejected by"))) {
                                Log.d("taskSetter",temp_work_description+"size is "+task_list.size());
                                final ModelClass_Task modelClass_task = new ModelClass_Task(temp_title, temp_description, temp_date, temp_priority, temp_status, temp_taskid, temp_assignid, temp_assignname, uid, name, temp_work_description, temp_flag, temp_createDate);
                                task_list.add(modelClass_task);
                                if (temp_flag.equals("1")) {

                                    FirebaseStorage.getInstance().getReference().child("document/*"+temp_taskid).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    temp_imageUri = uri;
                                                    Log.d("image_get",uri+"");
                                                    modelClass_task.setImage(temp_imageUri);

                                                    //todo store the retrieved image in object ang pass to modelclass.setimage

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("image_error",e.getMessage());
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
                            loading.setText("No Pending Tasks");
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
                Log.d("error",e.toString());
                loading.setText("Unable to Load");
            }
        });
    }

    private void getcreatedtask(){
        firebaseFirestore.collection("tasks").whereEqualTo("assigned_to",uid).orderBy("timestamp_1", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String temp_title, temp_description, temp_date, temp_priority, temp_creatorid, temp_creatorname, temp_status, temp_taskid, temp_work_description,temp_flag,temp_image, temp_createDate;
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
                            temp_work_description = documentSnapshot.getString("description_work");
                            temp_flag = documentSnapshot.getString("image");
                            Log.d("adapter",temp_flag);

                            Calendar calendar =  Calendar.getInstance();
                            java.util.Date c = calendar.getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            try {
                                Date due = df.parse(temp_date);
                                calendar.setTime(due);
                                calendar.add(Calendar.DATE,1);
                                due = calendar.getTime();
                                if (due.after(c)){
                                    Log.d("check","pending");
                                    if (temp_status.equals("Overdue")){
                                        temp_status = "pending";
                                        HashMap<String, Object> over = new HashMap<>();
                                        over.put("status",temp_status);
                                        firebaseFirestore.collection("tasks").document(temp_taskid).update(over).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("status","Staus updated to overdue");
                                            }
                                        });
                                    }
                                }
                                else {
                                    Log.d("check","overdue");
                                    if (temp_status.equals("pending")) {
                                        temp_status = "Overdue";
                                        HashMap<String, Object> over = new HashMap<>();
                                        over.put("status", temp_status);
                                        firebaseFirestore.collection("tasks").document(temp_taskid).update(over).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("status", "Staus updated to overdue");
                                            }
                                        });
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d("error_overdue",e.getMessage());
                            }

                            if (!temp_status.equals("Completed")&&(!temp_status.startsWith("Deleted"))&&(!temp_status.startsWith("Rejected by"))){
                                Log.d("taskSetter",temp_work_description+"size is "+task_list.size());
                            final ModelClass_Task modelClass_task = new ModelClass_Task(temp_title,temp_description,temp_date,temp_priority,temp_status,temp_taskid,uid,name,temp_creatorid,temp_creatorname,temp_work_description,temp_flag, temp_createDate);
                            task_list.add(modelClass_task);
                            if (temp_flag.equals("1")) {

                                FirebaseStorage.getInstance().getReference().child("document/*"+temp_taskid).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {


                                                temp_imageUri = uri;
                                                Log.d("image_get",uri+"");
                                                modelClass_task.setImage(temp_imageUri);

                                                //todo store the retrieved image in object ang pass to modelclass.setimage

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("image_error",e.getMessage());
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
                            loading.setText("No Pending Tasks");
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
                Log.d("error",e.toString());
                loading.setText("Unable to Load");
            }
        });
    }


}
