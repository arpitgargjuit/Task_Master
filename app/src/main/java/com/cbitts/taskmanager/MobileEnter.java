package com.cbitts.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MobileEnter extends AppCompatActivity {
    EditText mobileNumber;
    Button Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_enter);

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
                    Intent intent = new Intent(MobileEnter.this,verify_otp.class);
                    intent.putExtra("Mobile",MobileNumber);
                    Log.d("mobileEntered",MobileNumber);
                    startActivity(intent);
                }
            }
        });
    }
}