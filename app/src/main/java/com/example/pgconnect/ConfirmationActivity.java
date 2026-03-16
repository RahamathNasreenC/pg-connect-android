package com.example.pgconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        userEmail = getIntent().getStringExtra("user_email");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ConfirmationActivity.this, BookingTrackerActivity.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
            finish();
        }, 5000);
    }
}
