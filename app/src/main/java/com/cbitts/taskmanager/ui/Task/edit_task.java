package com.cbitts.taskmanager.ui.Task;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cbitts.taskmanager.MainActivity;
import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


public class edit_task extends Fragment implements AdapterView.OnItemSelectedListener,DatePickerDialog.OnDateSetListener {

    EditText title,description;
    Spinner spinner;
    Button add,select_date;
    String Priority;
    RecyclerView recyclerView;
    Add_task_generator addTaskGenerator = new Add_task_generator();
    ArrayList<String> Name = new ArrayList<>();
    ArrayList<String> Uid = new ArrayList<>();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String Date;
    ModelClass_Task task;

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        task = (ModelClass_Task) bundle.getSerializable("task");
        Toast.makeText(getContext(), task.getTitle(), Toast.LENGTH_SHORT).show();

        spinner = view.findViewById(R.id.priority_select);
        add = view.findViewById(R.id.add);
        title = view.findViewById(R.id.task_tittle);
        description = view.findViewById(R.id.task_description);
        recyclerView = view.findViewById(R.id.person_list);
        select_date = view.findViewById(R.id.select_date);
        spinner.setOnItemSelectedListener(this);

        title.setText(task.getTitle());
        if(!TextUtils.isEmpty(task.getDate())){
        select_date.setText(task.getDate());}
        if(!TextUtils.isEmpty(task.getDescription()))
        description.setText(task.getDescription());
        description.setHint("No Description");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.priority_select,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int spinnerPosition = spinner.getSelectedItemPosition();
        spinner.post(new Runnable() {
            @Override
        public void run() {
                if(task.getPriority().equals("High")) {
                    spinner.setSelection(1);
                }
        }
    });

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Title = title.getText().toString();
                String Description = description.getText().toString();
                if (TextUtils.isEmpty(Title)){
                    title.setError("Enter Title of the task");
                }
                else {
                    upload(Title,Description,Date,Priority,addTaskGenerator.getUid());
                }
            }
        });
    }
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE)
        );
        datePickerDialog.show();
    }

    private void upload(String title, String description, String date, String priority, final String uid) {
        final String TaskID = task.getTaskId();

        SharedPreferences getshared = this.getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);

        final Map<String, Object> task = new HashMap<>();
        task.put("assigned_to", uid);
        task.put("assigned_to_name", uid);
        task.put("title", title);
        task.put("description", description);
        task.put("due_date", date);
        task.put("priority", priority);
        task.put("created_by_uid", getshared.getString("uid", fAuth.getCurrentUser().getUid()));
        task.put("created_by_name", getshared.getString("name", "Not Available"));
        task.put("status", "Waiting acceptance");
        task.put("task_id", TaskID);

        edittask(task, TaskID);
    }
    private void edittask(Map<String, Object> task,String taskid) {
        DocumentReference documentReference = firebaseFirestore.collection("tasks").document(taskid);
        documentReference.update(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Task Updated Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Some Problem Occurred!!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(final String uid) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    //This is a Simple Logic to Send Notification different Device Programmatically....


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NGIwMDZmNjEtMDJjMC00ODM2LTgzMTEtODI4Zjg2NDIwNGNj");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"1daeff6a-541d-4c33-bc92-a0e7fe969ab8\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + fAuth.getCurrentUser().getUid() + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"English Message\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);
                        Toast.makeText(getContext(), "notification sent", Toast.LENGTH_SHORT).show();

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getuserdata();
    }



    private void getuserdata() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Name.clear();
        Uid.clear();
        firebaseFirestore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Name.add(documentSnapshot.getString("name"));
                            Uid.add(documentSnapshot.getString("id"));
                            name_adapter_recyclerView_edit adapter = new name_adapter_recyclerView_edit(getContext(),Name,Uid,addTaskGenerator, task.getAssigned_id(),task.getAssigned_name());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                });
    }

    private String randgenerate() {

        int i=0;
        final String characters = "qwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder result = new StringBuilder();
        while (i<20){
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i++;
        }
        return result.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(getContext(), ""+i, Toast.LENGTH_SHORT).show();
        if (i==0){
            Priority = "Normal";
        }
        else {
            Priority = "High";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Date = i2+"/"+(i1+1)+"/"+i;
        select_date.setText(Date);
        Log.d("Date is ",Date);
    }
}