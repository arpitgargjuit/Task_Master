package com.cbitts.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Details_enter extends AppCompatActivity {
    EditText name;
    Button submit;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_enter);

        name = findViewById(R.id.editText_name);
        submit = findViewById(R.id.button_submit);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Name = name.getText().toString().replaceAll("\n", " ");
                if (TextUtils.isEmpty(Name)) {
                    name.setError("Enter Your Name");
                } else {
                    final String uid = firebaseAuth.getCurrentUser().getUid();

                    FirebaseMessaging.getInstance().subscribeToTopic("updates");

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            final String newToken = instanceIdResult.getToken();
                            Log.d("new token", newToken);

                            DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", Name);
                            user.put("registered", "true");
                            user.put("mobile", firebaseAuth.getCurrentUser().getPhoneNumber());
                            user.put("id", uid);
                            user.put("token", newToken);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString("uid", uid);
                                    editor.putString("name", Name);
                                    editor.putString("number", getIntent().getStringExtra("mobile"));
                                    editor.putString("registered", "true");
                                    editor.apply();
                                    Intent intent = new Intent(Details_enter.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    });


                }
            }
        });
    }
}