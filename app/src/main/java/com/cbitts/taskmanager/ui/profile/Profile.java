package com.cbitts.taskmanager.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cbitts.taskmanager.Details_enter;
import com.cbitts.taskmanager.MainActivity;
import com.cbitts.taskmanager.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class Profile extends Fragment {


    EditText name;
    Button submit;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    SharedPreferences getshared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.editText_name);
        submit = view.findViewById(R.id.button_submit);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        getshared = getContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String Name = getshared.getString("name","Not available");
        name.setText(Name);

    }

    private void update() {
                final String Name = name.getText().toString().replaceAll("\n", " ");
                if (TextUtils.isEmpty(Name)) {
                    name.setError("Enter Your Name");
                } else {
                    final String uid = firebaseAuth.getCurrentUser().getUid();

                    DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", Name);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

//                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_details_name", MODE_PRIVATE);
                            SharedPreferences.Editor editor = getshared.edit();

                            editor.putString("name", Name);
                            editor.apply();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }

    }
}