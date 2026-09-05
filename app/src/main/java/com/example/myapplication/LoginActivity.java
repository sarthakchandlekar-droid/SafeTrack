package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_login);

        final EditText etName = findViewById(R.id.etName);
        final EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtRegister = findViewById(R.id.txtRegister);
        TextView txtForgot = findViewById(R.id.txtForgot);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        if (txtForgot != null) {
            txtForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Pre-fill username if passed from registration or saved
        String registeredUser = getIntent().getStringExtra("REGISTERED_USER");
        if (registeredUser == null || registeredUser.isEmpty()) {
            registeredUser = prefs.getString("PARENT_NAME", "");
        }
        if (registeredUser != null && !registeredUser.isEmpty()) {
            etName.setText(registeredUser);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etName.getText().toString().trim();
                String password = etPassword.getText().toString();

                boolean isValid = true;

                // Username validation
                if (username.isEmpty()) {
                    etName.setError("Username is required");
                    etName.requestFocus();
                    isValid = false;
                } else if (!containsLetters(username)) {
                    etName.setError("Username must contain valid letters");
                    etName.requestFocus();
                    isValid = false;
                }

                // Password validation
                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    if (isValid) {
                        etPassword.requestFocus();
                    }
                    isValid = false;
                }

                if (!isValid) {
                    return;
                }

                // Query Room Database for User
                UserDao userDao = AppDatabase.getInstance(LoginActivity.this).userDao();
                User user = userDao.getUserByName(username);
                if (user == null) {
                    user = userDao.getUserByPhone(username);
                }
                if (user == null) {
                    user = userDao.getFirstUser();
                }

                // Verification 1: Check if User exists in Database
                if (user == null) {
                    etName.setError("User account not found. Please register first.");
                    etName.requestFocus();
                    return;
                }

                // Verification 2: Password Comparison Verification Algorithm
                boolean isPasswordCorrect = PasswordUtils.verifyPassword(password, user.getPasswordHash(), user.getSalt());

                if (!isPasswordCorrect) {
                    etPassword.setError("Incorrect password. Please try again.");
                    etPassword.requestFocus();
                    Toast.makeText(LoginActivity.this, "Authentication failed: Incorrect password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If authentication succeeds, navigate to HomeActivity
                prefs.edit().putString("PARENT_NAME", user.getParentName()).apply();
                prefs.edit().putString("PARENT_PHONE", user.getParentPhone()).apply();

                String childName = user.getChildName();
                String childRelation = user.getParentRelation();
                String childDevice = user.getChildDevice();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("USER_NAME", user.getParentName());
                intent.putExtra("CHILD_NAME", childName);
                intent.putExtra("CHILD_RELATION", childRelation);
                intent.putExtra("CHILD_DEVICE", childDevice);
                startActivity(intent);
                finish();
            }
        });

        // Open RegisterActivity when clicking "New guardian? Create an account"
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean containsLetters(String text) {
        return text.matches(".*[a-zA-Z].*");
    }
}
