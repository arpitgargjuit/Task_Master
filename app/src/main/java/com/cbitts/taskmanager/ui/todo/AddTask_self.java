package com.cbitts.taskmanager.ui.todo;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.cbitts.taskmanager.NotificationHelper;
import com.cbitts.taskmanager.R;
import com.cbitts.taskmanager.ui.Task.Add_task_generator;
import com.cbitts.taskmanager.ui.Task.Custondialog_userSelect;
import com.cbitts.taskmanager.ui.Task.Name_ModelClass;
import com.cbitts.taskmanager.ui.Task.name_adapter_recyclerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class AddTask_self extends Fragment implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, Custondialog_userSelect.OnInputSelected {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText title, description;
    Spinner spinner;
    Button add, select_date, select_user;
    String Priority, imageurl;
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
        select_user.setVisibility(View.INVISIBLE);
        image = root.findViewById(R.id.image);
        spinner.setOnItemSelectedListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.priority_select, android.R.layout.simple_spinner_item);
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
                if (TextUtils.isEmpty(Title)) {
                    title.setError("Enter Title of the task");
                } else if (TextUtils.isEmpty(Description)) {
                    description.setError("Description is required");
                } else if (TextUtils.isEmpty(Date)) {
                    Toast.makeText(getContext(), "Please Select Due date", Toast.LENGTH_SHORT).show();
                } else if (Title.length() > 30) {
                    title.setError("Title Can't be more than 30 words");
                    Toast.makeText(getContext(), "Current words " + Title.length(), Toast.LENGTH_SHORT).show();
                } else if (Description.length() > 250) {
                    description.setError("Description can't be more than 250 words");
                    Toast.makeText(getContext(), "Current words " + Description.length(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        upload(Title, Description, Date, Priority);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
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

    private void upload(String title, String description, String date, String priority) throws ParseException {
        final String TaskID = randgenerate();

        SharedPreferences getshared = this.getActivity().getSharedPreferences("user_details", MODE_PRIVATE);

        final Map<String, Object> task = new HashMap<>();
        task.put("assigned_to", getshared.getString("uid", fAuth.getCurrentUser().getUid()));
        task.put("assigned_to_name", getshared.getString("name", "Not Available"));
        task.put("title", title);
        task.put("description", description);
        task.put("due_date", date);
        task.put("priority", priority);
        task.put("status", "Pending");
        task.put("task_id", TaskID);
        task.put("description_work", "");
        task.put("image", "" + flag);
        task.put("timestamp_created", Timestamp.now());
        java.util.Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        task.put("create_date", formattedDate);
        java.util.Date due = df.parse(date);
        task.put("Date_due",due);

        addnewtask(task, TaskID);

    }

    private void addnewtask(final Map<String, Object> task, final String taskid) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(task.get("assigned_to").toString()).collection("self_task").document(taskid);
        documentReference.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Task Created Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
                if (flag != 0)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageuri = data.getData();
            Picasso.get().load(imageuri)
                    .fit()
                    .centerCrop()
                    .into(image);
            flag = 1;
        }
    }

    private void uploadpicture(String task_id) {


        final StorageReference riversRef = storageReference.child("document/*" + task_id);

        riversRef.putFile(imageuri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return riversRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.d("image", downloadUri + "");
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        try {
                            Toast.makeText(getContext(), "Some Problem occurred in uploading image", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private String randgenerate() {

        int i = 0;
        final String characters = "qwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder result = new StringBuilder();
        while (i < 20) {
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i++;
        }
        return result.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(getContext(), ""+i, Toast.LENGTH_SHORT).show();
        if (i == 0) {
            Priority = "Normal";
        } else {
            Priority = "High";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Date = i2 + "/" + (i1 + 1) + "/" + i;
        select_date.setText(Date);
        Log.d("Date is ", Date);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void sendInput(Add_task_generator add_task_generator) {
        Log.d(TAG, "Found incoming input");
        this.addTaskGenerator = add_task_generator;
        select_user.setText(addTaskGenerator.getName());
    }
}