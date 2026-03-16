package com.example.pgconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeletePropertyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;
    List<Property> propertyList;
    PropertyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_property);

        recyclerView = findViewById(R.id.recyclerViewProperties);
        db = new DatabaseHelper(this);

        propertyList = db.getAllProperties();
        adapter = new PropertyAdapter(this, propertyList, db);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
