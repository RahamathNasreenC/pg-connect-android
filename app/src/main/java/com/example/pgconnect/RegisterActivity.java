package com.example.pgconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvLogin;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Name validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (name.length() < 3) {
            etName.setError("Name must be at least 3 characters");
            etName.requestFocus();
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            etName.setError("Name can contain only letters");
            etName.requestFocus();
            return;
        }

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

        if (!password.matches(".*[A-Z].*")) {
            etPassword.setError("Password must contain at least 1 uppercase letter");
            etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            etPassword.setError("Password must contain at least 1 lowercase letter");
            etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*\\d.*")) {
            etPassword.setError("Password must contain at least 1 number");
            etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            etPassword.setError("Password must contain at least 1 special character");
            etPassword.requestFocus();
            return;
        }

        // Confirm password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Email exists check
        if (db.isEmailExists(email)) {
            etEmail.setError("Email already registered");
            etEmail.requestFocus();
            return;
        }

        boolean inserted = db.insertUser(name, email, password);

        if (inserted) {
            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }

}
