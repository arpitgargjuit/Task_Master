package com.cbitts.taskmanager.ui.Task;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class add_task extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText title,description;
    Spinner spinner;
    Button add;
    String Priority;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);

        spinner = root.findViewById(R.id.priority_select);
        add = root.findViewById(R.id.add);
        title = root.findViewById(R.id.task_tittle);
        description = root.findViewById(R.id.task_description);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.priority_select,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = title.getText().toString();
                String Description = description.getText().toString();
                String Date = "010101";
                if (TextUtils.isEmpty(Title)){
                    title.setError("Enter Title of the task");
                }
                else {
                    upload(Title,Description,Date,Priority);
                }
            }
        });

        return root;
    }

    private void upload(String title, String description, String date, String priority) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String TaskID = randgenerate();
        DocumentReference documentReference = firebaseFirestore.collection("tasks").document("pending").collection(firebaseAuth.getCurrentUser().getUid()).document(TaskID);
        Map<String,Object> task = new HashMap<>();
        task.put("title",title);
        task.put("description",description);
        task.put("due_date",date);
        task.put("priority",priority);
        task.put("created_by",firebaseAuth.getCurrentUser().getUid());
        documentReference.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Task Created Successfully", Toast.LENGTH_SHORT).show();
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
}