package com.example.pgconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    Button btnAddProperty, btnDeleteProperty, btnBookingRequests, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnAddProperty = findViewById(R.id.btnAddProperty);
        btnDeleteProperty = findViewById(R.id.btnDeleteProperty);
        btnBookingRequests = findViewById(R.id.btnBookingRequests);
        btnLogout = findViewById(R.id.btnLogout);

        btnAddProperty.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AddPropertyActivity.class));
        });

        btnDeleteProperty.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, DeletePropertyActivity.class));
        });

        // NEW Booking Requests Page
        btnBookingRequests.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminBookingRequestsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });
    }
}
