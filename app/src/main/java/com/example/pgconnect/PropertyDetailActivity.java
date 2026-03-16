package com.example.pgconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

public class PropertyDetailActivity extends AppCompatActivity {

    ImageView imgPropertyDetail, imgBedType, btnBack, imgLocationIcon;
    TextView txtPgName, txtLocation, txtRent, txtRooms, txtDescription, txtContact;
    Button btnBookNow;
    LinearLayout layoutAmenities;
    DatabaseHelper dbHelper;

    int propertyId;
    String title, location, description, amenities, contact, propertyImagePath, bedImagePath, roomType;
    int rent, rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        imgPropertyDetail = findViewById(R.id.imgPropertyDetail);
        imgBedType = findViewById(R.id.imgBedType);
        btnBack = findViewById(R.id.btnBack);
        imgLocationIcon = findViewById(R.id.imgLocationIcon);

        txtPgName = findViewById(R.id.txtPgName);
        txtLocation = findViewById(R.id.txtLocation);
        txtRent = findViewById(R.id.txtRent);
        txtRooms = findViewById(R.id.txtRooms);
        txtDescription = findViewById(R.id.txtDescription);
        txtContact = findViewById(R.id.txtContact);

        layoutAmenities = findViewById(R.id.layoutAmenities);
        btnBookNow = findViewById(R.id.btnBookNow);
        dbHelper = new DatabaseHelper(this);

        propertyId = getIntent().getIntExtra("id", 0);
        title = getIntent().getStringExtra("title");
        location = getIntent().getStringExtra("location");
        rent = getIntent().getIntExtra("rent", 0);
        rooms = getIntent().getIntExtra("rooms", 0);
        description = getIntent().getStringExtra("description");
        amenities = getIntent().getStringExtra("amenities");
        contact = getIntent().getStringExtra("contact");
        propertyImagePath = getIntent().getStringExtra("propertyImage");
        bedImagePath = getIntent().getStringExtra("bedImage");
        roomType = getIntent().getStringExtra("roomType");
        TextView txtRoomType = findViewById(R.id.txtRoomType);
        txtRoomType.setText(roomType != null ? roomType : ""); // Only show admin-provided value


        txtPgName.setText(title != null ? title : "No Title");
        txtLocation.setText(location != null ? location : "No Location");
        txtRent.setText("Rs." + rent + "/mn");
        updateLiveRoomCount();
        txtDescription.setText(description != null ? description : "No Description");
        txtContact.setText("Contact: " + (contact != null ? contact : "Not Available"));

        if (propertyImagePath != null && !propertyImagePath.isEmpty()) {
            Glide.with(this).load(new File(propertyImagePath)).into(imgPropertyDetail);
        }

        if (bedImagePath != null && !bedImagePath.isEmpty()) {
            Glide.with(this).load(new File(bedImagePath)).into(imgBedType);
        }

        if (amenities != null && !amenities.isEmpty()) {
            String[] amenityList = amenities.split(",");
            for (String amenity : amenityList) {
                addAmenityWithIcon(amenity.trim());
            }
        }

        btnBack.setOnClickListener(v -> finish());

        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(PropertyDetailActivity.this, UserBookingActivity.class);
            intent.putExtra("property_id", propertyId);
            intent.putExtra("pg_name", title);
            intent.putExtra("location", location);
            intent.putExtra("rent", rent);
            intent.putExtra("room_type", roomType);
            intent.putExtra("pg_image", propertyImagePath);
            startActivity(intent);
        });

        imgLocationIcon.setOnClickListener(v -> {
            if (location != null && !location.isEmpty()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location))));
                }
            }
        });
    }

    private void addAmenityWithIcon(String amenityName) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 10, 0, 10);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(50, 50));

        switch (amenityName.toLowerCase()) {
            case "wifi": icon.setImageResource(R.drawable.ic_baseline_wifi_24); break;
            case "food": icon.setImageResource(R.drawable.ic_baseline_restaurant_24); break;
            case "ac": icon.setImageResource(R.drawable.ic_baseline_ac_unit_24); break;
            case "laundry": icon.setImageResource(R.drawable.ic_baseline_local_laundry_service_24); break;
            case "security": icon.setImageResource(R.drawable.ic_baseline_security_24); break;
            default: icon.setImageResource(R.drawable.ic_baseline_security_24);
        }

        TextView text = new TextView(this);
        text.setText(amenityName);
        text.setTextSize(15);
        text.setTextColor(getResources().getColor(android.R.color.black));
        text.setPadding(20, 0, 0, 0);

        row.addView(icon);
        row.addView(text);
        layoutAmenities.addView(row);
    }

    private void updateLiveRoomCount() {
        Property property = dbHelper.getPropertyById(propertyId);
        if (property != null) {
            txtRooms.setText("Available Rooms: " + property.getAvailableRooms());
        } else {
            txtRooms.setText("Available Rooms: " + rooms);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLiveRoomCount(); // Refresh after booking/admin changes
    }
}
