package com.example.pgconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerNotifications;
    DatabaseHelper db;
    ArrayList<NotificationItem> notificationsList;
    NotificationAdapter adapter;
    String userEmail;
    TextView tvNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerNotifications = findViewById(R.id.recyclerNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);

        db = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("user_email");

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email missing! Cannot load notifications.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadNotifications();
    }

    private void loadNotifications() {
        notificationsList = new ArrayList<>(db.getNotificationsByUser(userEmail));

        if (notificationsList.isEmpty()) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            recyclerNotifications.setVisibility(View.GONE);
        } else {
            tvNoNotifications.setVisibility(View.GONE);
            recyclerNotifications.setVisibility(View.VISIBLE);

            adapter = new NotificationAdapter(this, notificationsList);
            recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
            recyclerNotifications.setAdapter(adapter);

            // 🔥 MARK ALL AS READ (THIS RESETS BADGE TO 0)
            for (NotificationItem n : notificationsList) {
                if (n.getIsRead() == 0) {
                    db.markNotificationAsRead(n.getId());
                    n.setIsRead(1);
                }
            }
        }
    }
}
