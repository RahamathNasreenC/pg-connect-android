package com.example.pgconnect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import java.util.List;

public class BookingTrackerActivity extends AppCompatActivity {

    RecyclerView recyclerBookingTracker;
    BookingTrackerAdapter adapter;

    DatabaseHelper dbHelper;
    String userEmail;
    ImageView btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_tracker);


        // RecyclerView setup
        recyclerBookingTracker = findViewById(R.id.recyclerBookingTracker);
        recyclerBookingTracker.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("user_email");

        loadBookings();
    }


    private void loadBookings() {
        List<Booking> bookingList = dbHelper.getBookingsByUser(userEmail);
        adapter = new BookingTrackerAdapter(this, bookingList);
        recyclerBookingTracker.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}
