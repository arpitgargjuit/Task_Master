package com.cbitts.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class verify_otp extends AppCompatActivity {

    PinView editTextCode;
    FirebaseAuth mAuth;
    Button signIn;
//    EditText editTextCode;
    private String mVerificationId;
    FirebaseFirestore firebaseFirestore;
    String mobile;
    String TAG = "verify is this";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        mAuth = FirebaseAuth.getInstance();
        signIn = findViewById(R.id.buttonSignIn);
        editTextCode = findViewById(R.id.editTextCode);
//        pinView = findViewById(R.id.pin_view);
//        pinView.setText("0101");
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        mobile = intent.getStringExtra("Mobile");
        sendVerificationCode(mobile);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }
                Log.d("Code",code);
                Log.d(TAG,"test "+mVerificationId);
//                editTextCode.setError("Verified");
                verifyVerificationCode(code);
            }
        });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG,"inside onverification  "+phoneAuthCredential);

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(verify_otp.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            Log.d(TAG,"id id "+s);
            Log.d(TAG,"Inside oncodesent");
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        Log.d(TAG,"Inside verification code with code as :"+ code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        Log.d(TAG,"hello ");

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(verify_otp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            final String uid = mAuth.getCurrentUser().getUid();
                            firebaseFirestore.collection("users").document(uid).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                            String register = documentSnapshot.getString("registered");
                                            try {
                                                Log.d(TAG, "try + " + register);
                                                if (register.equals("true")) {
                                                    Log.d(TAG,"Inside 1st if_try");
                                                    final Intent intent = new Intent(verify_otp.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    //Saving Id and name in shared resources

                                                    SharedPreferences sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
                                                    final SharedPreferences.Editor editor = sharedPreferences.edit();

                                                    FirebaseMessaging.getInstance().subscribeToTopic("updates");

                                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                                            String newToken = instanceIdResult.getToken();
                                                            Log.d("new token",newToken);
                                                            Map<String, Object> token = new HashMap<>();
                                                            token.put("token",newToken);
                                                            firebaseFirestore.collection("users").document(uid).update(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //nothing
                                                                    startActivity(intent);
                                                                }
                                                            });
//                                                            getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
                                                        }
                                                    });

//                                                    Log.d("newToken", getActivity().getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));



                                                    editor.putString("uid",uid);
                                                    editor.putString("name",documentSnapshot.getString("name"));
                                                    editor.putString("number",mobile);
                                                    editor.putString("registered","true");
                                                    editor.apply();
                                                } else {
                                                    Log.d(TAG,"Inside else_try");
                                                    Intent intent = new Intent(verify_otp.this, Details_enter.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("mobile",mobile);
                                                    startActivity(intent);
                                                }
                                            }
                                            catch (Exception e ){
                                                Log.d(TAG,"Exception is "+e.toString());
                                                Intent intent = new Intent(verify_otp.this, Details_enter.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"Failed block ran with  "+e);
                                }
                            });
                            Log.d(TAG,"success");

                        } else {

                            //verification unsuccessful.. display an error message
                            Log.d(TAG,"inside else @task unsuccessful");

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
//                            snackbar.show();
                        }
                    }
                });
    }


    ////////////////////////////////

//    private void sendVerificationCode(String mobile) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+91" + mobile,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallbacks);
//    }
//
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            //Getting the code sent by SMS
//            String code = phoneAuthCredential.getSmsCode();
//
//            //sometime the code is not detected automatically
//            //in this case the code will be null
//            //so user has to manually enter the code
//            if (code != null) {
//                editTextCode.setText(code);
//                //verifying the code
//                verifyVerificationCode(code);
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(verify_otp.this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            mVerificationId = s;
//            Log.d("Code Status",s);
////            mResendToken = forceResendingToken;
//        }
//    };
//
//    private void verifyVerificationCode(String otp) {
//        //creating the credential
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
//
//        //signing the user
//        signInWithPhoneAuthCredential(credential);
//
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(verify_otp.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            String uid = mAuth.getCurrentUser().getUid();
//                            firebaseFirestore.collection("users").document(uid).get()
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                            String register = documentSnapshot.getString("registered");
//                                            try {
//                                                Log.d("Value is", "" + register);
//                                                if (register.equals("true")) {
//                                                    Log.d("Value is","Inside 1st if_try");
//                                                    Intent intent = new Intent(verify_otp.this, MainActivity.class);
//                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                    startActivity(intent);
//                                                } else {
//                                                    Log.d("Value","Inside else_try");
//                                                    Intent intent = new Intent(verify_otp.this, Details_enter.class);
//                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                    startActivity(intent);
//                                                }
//                                            }
//                                            finally {
//                                                Log.d("Value is","Inside finally");
//                                                Intent intent = new Intent(verify_otp.this, Details_enter.class);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                startActivity(intent);
//                                            }
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.d("fail","Failed block ran with  "+e);
//                                        }
//                                    });
//
//                            //verification successful we will start the profile activity
//
//                        } else {
//
//                            //verification unsuccessful.. display an error message
//
//                            String message = "Somthing is wrong, we will fix it soon...";
//
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                message = "Invalid code entered...";
//                            }
//
//                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
//                            snackbar.setAction("Dismiss", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            });
//                            snackbar.show();
//                        }
//                    }
//                });
//    }

}