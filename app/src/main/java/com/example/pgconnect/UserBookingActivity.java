package com.example.pgconnect;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class UserBookingActivity extends AppCompatActivity {

    TextView txtPgName, txtLocation, txtRent, txtFileName;
    EditText edtEmail, edtMobile;
    Button btnChooseFile, btnSubmitBooking;
    ImageView imgPg;

    Uri selectedFileUri = null;

    int propertyId, rent;
    String pgName, location, roomType;
    String pgImagePath;

    DatabaseHelper dbHelper;

    ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        imgPg = findViewById(R.id.imgPg);

        txtPgName = findViewById(R.id.txtPgName);
        txtLocation = findViewById(R.id.txtLocation);
        txtRent = findViewById(R.id.txtRent);
        txtFileName = findViewById(R.id.txtFileName);

        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);

        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmitBooking = findViewById(R.id.btnSubmitBooking);

        dbHelper = new DatabaseHelper(this);

        // Get property details
        propertyId = getIntent().getIntExtra("property_id", 0);
        pgName = getIntent().getStringExtra("pg_name");
        location = getIntent().getStringExtra("location");
        rent = getIntent().getIntExtra("rent", 0);
        roomType = getIntent().getStringExtra("room_type");

        // PG IMAGE PATH RECEIVED
        pgImagePath = getIntent().getStringExtra("pg_image");

        txtPgName.setText(pgName);
        txtLocation.setText(location);
        txtRent.setText("Rent: Rs." + rent);



        // SET IMAGE
        if (pgImagePath != null && !pgImagePath.isEmpty()) {
            try {
                Uri imgUri = Uri.parse(pgImagePath);
                imgPg.setImageURI(imgUri);
            } catch (Exception e) {
                imgPg.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            imgPg.setImageResource(R.drawable.ic_launcher_background);
        }

        // File picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        selectedFileUri = uri;

                        try {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        txtFileName.setText("Selected: " + getFileName(uri));
                    }
                }
        );

        btnChooseFile.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            });
        });

        btnSubmitBooking.setOnClickListener(v -> submitBooking());
    }

    private void submitBooking() {

        String email = edtEmail.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Enter Email");
            return;
        }

        if (mobile.isEmpty()) {
            edtMobile.setError("Enter Mobile Number");
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Please upload ID proof (PDF/Word)", Toast.LENGTH_SHORT).show();
            return;
        }

        String idProofPath = selectedFileUri.toString();

        boolean inserted = dbHelper.insertBooking(
                propertyId,
                pgName,
                location,
                rent,
                roomType,
                email,
                mobile,
                idProofPath
        );

        if (inserted) {
            Toast.makeText(this, "Booking Request Sent!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserBookingActivity.this, ConfirmationActivity.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Booking Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = "ID Proof File";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
            cursor.close();
        }
        return result;
    }
}
