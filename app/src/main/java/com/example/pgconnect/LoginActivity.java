package com.example.pgconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin, btnUser, btnAdmin;
    MaterialButtonToggleGroup toggleGroup;
    TextView tvSignup;

    DatabaseHelper db;

    boolean isAdminSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        toggleGroup = findViewById(R.id.toggleGroup);
        btnUser = findViewById(R.id.btnUser);
        btnAdmin = findViewById(R.id.btnAdmin);

        tvSignup = findViewById(R.id.tvSignup);

        // Default selection = User
        toggleGroup.check(R.id.btnUser);
        isAdminSelected = false;
        tvSignup.setVisibility(View.VISIBLE);

        // Default button colors
        btnUser.setBackgroundTintList(getColorStateList(R.color.blue));
        btnUser.setTextColor(getColor(R.color.white));

        btnAdmin.setBackgroundTintList(getColorStateList(R.color.gray));
        btnAdmin.setTextColor(getColor(R.color.black));

        // ToggleGroup Listener
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {

            if (checkedId == R.id.btnAdmin && isChecked) {

                isAdminSelected = true;
                tvSignup.setVisibility(View.GONE);

                btnAdmin.setBackgroundTintList(getColorStateList(R.color.blue));
                btnAdmin.setTextColor(getColor(R.color.white));

                btnUser.setBackgroundTintList(getColorStateList(R.color.gray));
                btnUser.setTextColor(getColor(R.color.black));
            }
            else if (checkedId == R.id.btnUser && isChecked) {

                isAdminSelected = false;
                tvSignup.setVisibility(View.VISIBLE);

                btnUser.setBackgroundTintList(getColorStateList(R.color.blue));
                btnUser.setTextColor(getColor(R.color.white));

                btnAdmin.setBackgroundTintList(getColorStateList(R.color.gray));
                btnAdmin.setTextColor(getColor(R.color.black));
            }
        });

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Email validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return;
        }

        // Password validation
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            etPassword.requestFocus();
            return;
        }

        boolean valid = db.checkUser(email, password);

        if (!valid) {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Admin login
        if (isAdminSelected && email.equals("admin@pg.com")) {

            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            intent.putExtra("admin_email", email);
            startActivity(intent);

        }
        // User login
        else if (!isAdminSelected && !email.equals("admin@pg.com")) {

            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
            intent.putExtra("user_email", email);
            startActivity(intent);

        }
        // Wrong role selected
        else {
            Toast.makeText(this, "Selected role does not match account", Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
    }
}
