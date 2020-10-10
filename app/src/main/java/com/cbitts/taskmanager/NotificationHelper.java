package com.cbitts.taskmanager;


import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;


public class NotificationHelper {

    Context context;
    FirebaseFirestore firebaseFirestore;

    public NotificationHelper(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public NotificationHelper() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void sendNotificationTune1(String uid, String title){
        sendNotification(uid,title);
    }

    public void sendNotificationTune2(String uid, String title){
        sendNotification(uid,title);
    }

    private void sendNotification(final String uid, final String title) {

//        Toast.makeText(getContext(), "Current Recipients is : user1@gmail.com ( Just For Demo )", Toast.LENGTH_SHORT).show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
//                    if (MainActivity.LoggedIn_User_Email.equals("user1@gmail.com")) {
//                        send_email = "user2@gmail.com";
//                    } else {
//                        send_email = "user1@gmail.com";
//                    }
                    send_email = uid;

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

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"" + title + "\"},"
                                + "\"small_icon\":\"task_master_icon\","
                                +"\"large_icon\":\"task_master_icon\""
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
                        RandGenerate randGenerate = new RandGenerate();

                        HashMap<String,Object> noti = new HashMap<>();


                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");

                        noti.put("id",uid);
                        noti.put("message",title);
                        noti.put("date",sdf.format(cal.getTime())+" at "+currentTime);
                        noti.put("timestamp_1", Timestamp.now());

                        firebaseFirestore.collection("notifications").document(randGenerate.randgenerate()).set(noti).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("notification","Uploaded to database");
                            }
                        });


                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}

