package com.cbitts.taskmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MobileEnter extends AppCompatActivity {
    EditText mobileNumber;
    Button Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_enter);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        mobileNumber = findViewById(R.id.mobile_number);
        Next = findViewById(R.id.next);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MobileNumber = mobileNumber.getText().toString().trim();
                if(TextUtils.isEmpty(MobileNumber)){
                    Toast.makeText(MobileEnter.this, "Enter the Mobile Number", Toast.LENGTH_SHORT).show();
                }
                else if (MobileNumber.length()!=10)
                {
                    mobileNumber.setError("Enter Correct Number");
                }
                else {
                    if (internetConnectionAvailable(10000)) {
                        Intent intent = new Intent(MobileEnter.this, verify_otp.class);
                        intent.putExtra("Mobile", MobileNumber);
                        Log.d("mobileEntered", MobileNumber);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(MobileEnter.this, "Internet not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
}