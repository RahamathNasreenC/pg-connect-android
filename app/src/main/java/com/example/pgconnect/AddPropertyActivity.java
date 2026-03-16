package com.example.pgconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddPropertyActivity extends AppCompatActivity {

    EditText etTitle, etLocation, etRoomType, etRent;

    EditText etDescription, etAvailableRooms, etContactNumber;

    CheckBox cbWifi, cbFood, cbAC, cbLaundry, cbSecurity;

    Button btnAddProperty, btnSelectImage, btnSelectBedImage;

    ImageView ivPropertyImage, ivBedImage;

    Uri imageUri, bedImageUri;

    String savedImagePath = "";
    String savedBedImagePath = "";

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        db = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etRoomType = findViewById(R.id.etRoomType);
        etRent = findViewById(R.id.etRent);

        etDescription = findViewById(R.id.etDescription);
        etAvailableRooms = findViewById(R.id.etAvailableRooms);
        etContactNumber = findViewById(R.id.etContactNumber);

        cbWifi = findViewById(R.id.cbWifi);
        cbFood = findViewById(R.id.cbFood);
        cbAC = findViewById(R.id.cbAC);
        cbLaundry = findViewById(R.id.cbLaundry);
        cbSecurity = findViewById(R.id.cbSecurity);

        ivPropertyImage = findViewById(R.id.ivPropertyImage);
        ivBedImage = findViewById(R.id.ivBedImage);

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectBedImage = findViewById(R.id.btnSelectBedImage);

        btnAddProperty = findViewById(R.id.btnAddPropertySave);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(AddPropertyActivity.this, AdminDashboardActivity.class));
            finish();
        });

        btnSelectImage.setOnClickListener(v -> openImageChooser(100));
        btnSelectBedImage.setOnClickListener(v -> openImageChooser(200));

        btnAddProperty.setOnClickListener(v -> addProperty());
    }

    private void openImageChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == 100) {
                imageUri = data.getData();
                ivPropertyImage.setImageURI(imageUri);
                savedImagePath = saveImageToInternalStorage(imageUri, "PGImages");
            }

            if (requestCode == 200) {
                bedImageUri = data.getData();
                ivBedImage.setImageURI(bedImageUri);
                savedBedImagePath = saveImageToInternalStorage(bedImageUri, "BedImages");
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri, String folderName) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            File folder = new File(getFilesDir(), folderName);
            if (!folder.exists()) folder.mkdirs();

            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(folder, fileName);

            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            return imageFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Image save failed", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void addProperty() {

        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String roomType = etRoomType.getText().toString().trim();
        String rentStr = etRent.getText().toString().trim();

        String description = etDescription.getText().toString().trim();
        String availableRoomsStr = etAvailableRooms.getText().toString().trim();
        String contactNumber = etContactNumber.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || roomType.isEmpty() || rentStr.isEmpty()
                || description.isEmpty() || availableRoomsStr.isEmpty() || contactNumber.isEmpty()) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (savedImagePath.isEmpty()) {
            Toast.makeText(this, "Please select property image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (savedBedImagePath.isEmpty()) {
            Toast.makeText(this, "Please select bed image", Toast.LENGTH_SHORT).show();
            return;
        }

        int rent;
        int availableRooms;

        try {
            rent = Integer.parseInt(rentStr);
            availableRooms = Integer.parseInt(availableRoomsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // amenities
        StringBuilder amenities = new StringBuilder();
        if (cbWifi.isChecked()) amenities.append("Wifi,");
        if (cbFood.isChecked()) amenities.append("Food,");
        if (cbAC.isChecked()) amenities.append("AC,");
        if (cbLaundry.isChecked()) amenities.append("Laundry,");
        if (cbSecurity.isChecked()) amenities.append("Security,");

        if (amenities.length() > 0)
            amenities.setLength(amenities.length() - 1);

        boolean inserted = db.insertProperty(
                title, location, roomType, rent,
                amenities.toString(), savedImagePath,
                description, availableRooms, contactNumber,
                savedBedImagePath
        );

        if (inserted) {
            Toast.makeText(this, "Property added successfully", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(this, "Failed to add property", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {

        etTitle.setText("");
        etLocation.setText("");
        etRoomType.setText("");
        etRent.setText("");

        etDescription.setText("");
        etAvailableRooms.setText("");
        etContactNumber.setText("");

        cbWifi.setChecked(false);
        cbFood.setChecked(false);
        cbAC.setChecked(false);
        cbLaundry.setChecked(false);
        cbSecurity.setChecked(false);

        ivPropertyImage.setImageResource(0);
        ivBedImage.setImageResource(0);

        imageUri = null;
        bedImageUri = null;

        savedImagePath = "";
        savedBedImagePath = "";
    }
}
