package com.example.pgconnect;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminBookingRequestsActivity extends AppCompatActivity {
    Spinner spinnerFilter;
    RecyclerView recyclerBookingRequests;
    AdminBookingAdapter adapter;
    DatabaseHelper db;
    ImageView btnBack;

    String[] filterOptions = {"All", "Pending", "Approved", "Rejected"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking_requests);

        recyclerBookingRequests = findViewById(R.id.recyclerBookingRequests);
        btnBack = findViewById(R.id.btnBack);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        db = new DatabaseHelper(this);

        recyclerBookingRequests.setLayoutManager(new LinearLayoutManager(this));

        // Spinner Adapter
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        filterOptions);

        spinnerFilter.setAdapter(spinnerAdapter);

        // ⭐ Default selection = ALL
        spinnerFilter.setSelection(0);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected = filterOptions[position];
                List<Booking> bookingList;

                if (selected.equals("All")) {
                    bookingList = db.getAllBookings();
                    bookingList = sortBookingsPriority(bookingList); // Pending on top
                } else {
                    bookingList = db.getBookingsByStatus(selected);
                }

                adapter = new AdminBookingAdapter(
                        AdminBookingRequestsActivity.this,
                        bookingList,
                        db
                );
                recyclerBookingRequests.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // ⭐ Smart Sorting: Pending → Approved → Rejected
    private List<Booking> sortBookingsPriority(List<Booking> list) {
        List<Booking> sortedList = new ArrayList<>(list);

        Collections.sort(sortedList, new Comparator<Booking>() {
            @Override
            public int compare(Booking b1, Booking b2) {
                return getPriority(b1.getStatus()) - getPriority(b2.getStatus());
            }
        });

        return sortedList;
    }

    private int getPriority(String status) {
        if (status.equalsIgnoreCase("Pending")) return 0;
        if (status.equalsIgnoreCase("Approved")) return 1;
        if (status.equalsIgnoreCase("Rejected")) return 2;
        return 3;
    }

    @Override
    protected void onResume() {
        super.onResume();

        String selected = spinnerFilter.getSelectedItem().toString();
        List<Booking> bookingList;

        if (selected.equals("All")) {
            bookingList = db.getAllBookings();
            bookingList = sortBookingsPriority(bookingList);
        } else {
            bookingList = db.getBookingsByStatus(selected);
        }

        adapter = new AdminBookingAdapter(this, bookingList, db);
        recyclerBookingRequests.setAdapter(adapter);
    }
}
