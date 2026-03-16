package com.example.pgconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserDashboardActivity extends AppCompatActivity {

    RecyclerView recyclerProperties;
    DatabaseHelper db;
    ArrayList<Property> propertyList, filteredList;
    UserPropertyAdapter adapter;

    EditText etSearch;
    ImageView imgProfile, imgNotification;
    TextView tvNotificationBadge;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        recyclerProperties = findViewById(R.id.recyclerProperties);
        etSearch = findViewById(R.id.etSearch);
        imgProfile = findViewById(R.id.imgProfile);
        imgNotification = findViewById(R.id.imgNotification);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);

        db = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("user_email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email missing! Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        propertyList = new ArrayList<>(db.getAllProperties());
        filteredList = new ArrayList<>(propertyList);

        adapter = new UserPropertyAdapter(this, filteredList);
        recyclerProperties.setLayoutManager(new LinearLayoutManager(this));
        recyclerProperties.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProperties(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        imgProfile.setOnClickListener(this::showProfileMenu);

        // 🔔 Open Notification Screen
        imgNotification.setOnClickListener(v -> openNotifications());

        // 🔴 INITIAL BADGE LOAD
        updateNotificationBadge();
    }

    private void filterProperties(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(propertyList);
        } else {
            for (Property p : propertyList) {
                if (p.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        p.getLocation().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showProfileMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_my_bookings) {
                startActivity(new Intent(this, BookingTrackerActivity.class)
                        .putExtra("user_email", userEmail));
                return true;
            } else if (item.getItemId() == R.id.menu_logout) {
                startActivity(new Intent(this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void openNotifications() {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("user_email", userEmail);
        startActivity(intent);
    }

    // 🔴 REAL NOTIFICATION BADGE COUNT (IMPORTANT)
    private void updateNotificationBadge() {
        ArrayList<NotificationItem> notifications =
                new ArrayList<>(db.getNotificationsByUser(userEmail));

        int unreadCount = 0;

        for (NotificationItem n : notifications) {
            if (n.getIsRead() == 0) {
                unreadCount++;
            }
        }

        if (unreadCount > 0) {
            tvNotificationBadge.setVisibility(View.VISIBLE);

            // If more than 9 notifications show 9+
            if (unreadCount > 9) {
                tvNotificationBadge.setText("9+");
            } else {
                tvNotificationBadge.setText(String.valueOf(unreadCount));
            }
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }

    // 🔥 AUTO REFRESH BADGE WHEN RETURNING FROM NOTIFICATION SCREEN
    @Override
    protected void onResume() {
        super.onResume();
        propertyList = new ArrayList<>(db.getAllProperties());
        filterProperties(etSearch.getText().toString());

        // VERY IMPORTANT FOR REAL-TIME BADGE UPDATE
        updateNotificationBadge();
    }
}
