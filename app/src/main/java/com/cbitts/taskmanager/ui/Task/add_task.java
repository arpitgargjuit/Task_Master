package com.cbitts.taskmanager.ui.Task;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class add_task extends Fragment implements AdapterView.OnItemSelectedListener,DatePickerDialog.OnDateSetListener,Custondialog_userSelect.OnInputSelected {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText title,description;
    Spinner spinner;
    Button add,select_date,select_user;
    String Priority;
    RecyclerView recyclerView;
//    SharedPreferences mPrefs;
    Add_task_generator addTaskGenerator = new Add_task_generator();
    ArrayList<Name_ModelClass> Assign_name = new ArrayList<>();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String Date;
    final String TAG = "addTaskFragment";
    ImageView image;
    StorageReference storageReference;
    int flag = 0;
    Uri imageuri;
//    String Name_creator = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);

        spinner = root.findViewById(R.id.priority_select);
        add = root.findViewById(R.id.add);
        title = root.findViewById(R.id.task_tittle);
        description = root.findViewById(R.id.task_description);
        recyclerView = root.findViewById(R.id.person_list);
        select_date = root.findViewById(R.id.select_date);
        select_user = root.findViewById(R.id.select_user);
        image = root.findViewById(R.id.image);
        spinner.setOnItemSelectedListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();





        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.priority_select,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                else if (TextUtils.isEmpty(addTaskGenerator.getUid())){
                    Toast.makeText(getContext(), "Select the user", Toast.LENGTH_SHORT).show();
                }
                else {
                    upload(Title,Description,Date,Priority,addTaskGenerator.getUid());
                }
            }
        });

        select_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDialog();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        return root;
    }

    private void showUserDialog() {
        Custondialog_userSelect custondialog_userSelect = new Custondialog_userSelect();
        custondialog_userSelect.setTargetFragment(this,1);
        custondialog_userSelect.show(getParentFragmentManager(),"Test");
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void upload(String title, String description, String date, String priority, final String uid) {
        final String TaskID = randgenerate();

        SharedPreferences getshared = this.getActivity().getSharedPreferences("user_details", MODE_PRIVATE);

        final Map<String,Object> task = new HashMap<>();
        task.put("assigned_to",uid);
        task.put("assigned_to_name",uid);
        task.put("title",title);
        task.put("description",description);
        task.put("due_date",date);
        task.put("priority",priority);
        task.put("created_by_uid",getshared.getString("uid",fAuth.getCurrentUser().getUid()));
        task.put("created_by_name",getshared.getString("name","Not Available"));
        task.put("status","Waiting acceptance");
        task.put("task_id",TaskID);
        task.put("description_work","");
        task.put("image",""+flag);

        addnewtask(task,TaskID);

    }

    private void addnewtask(Map<String, Object> task, final String taskid) {
        DocumentReference documentReference = firebaseFirestore.collection("tasks").document(taskid);
        documentReference.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Task Created Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
                if (flag!=0)
                    uploadpicture(taskid);
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
//        getuserdata();
    }



    private void getuserdata() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER",MODE_PRIVATE);
//
//
//        Gson gson = new Gson();
//        String json = mPrefs.getString("MyObject", "");
//        ArrayList<Name_ModelClass> obj = gson.fromJson(json, MyObject.class);

//            Gson gson = new Gson();
//            String json = sharedPreferences.getString("Set", "");
//            if (json.isEmpty()) {
//                Toast.makeText(getContext(),"There is something error",Toast.LENGTH_LONG).show();
//            } else {
//                Type type = new TypeToken<List<Name_ModelClass>>() {
//                }.getType();
//                Assign_name = gson.fromJson(json, type);
//                for(String data:arrPackageData) {
//                    result.setText(data);
//                }

        Assign_name.clear();
        firebaseFirestore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String tempName,tempUid;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            tempName = documentSnapshot.getString("name");
                            tempUid = documentSnapshot.getString("id");
                            Assign_name.add(new Name_ModelClass(tempName,tempUid));
                            name_adapter_recyclerView adapter = new name_adapter_recyclerView(getContext(),Assign_name,addTaskGenerator);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
//                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(Assign_name); // myObject - instance of MyObject
//                        prefsEditor.putString("MyObject", json);
//                        prefsEditor.commit();

//                        Gson gson = new Gson();
//                        String json = gson.toJson(Assign_name);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("Set",json );
//                        editor.commit();

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            Picasso.with(getContext()).load(imageuri)
                    .fit()
                    .centerCrop()
                    .into(image);
            flag = 1;
        }
    }

    private void uploadpicture(String task_id) {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uplading image ...");
        pd.show();

        StorageReference riversRef = storageReference.child("document/*"+task_id);

        riversRef.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
//                        finish();
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
//                        finish();
                        // Handle unsuccessful uploads
                        // ...
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPercentage = (100.0 *taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                pd.setMessage("Percentage: "+(int)progressPercentage+" %");
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    public void sendInput(Add_task_generator add_task_generator) {
        Log.d(TAG,"Found incoming input");
        this.addTaskGenerator = add_task_generator;
        select_user.setText(addTaskGenerator.getName());
    }
}