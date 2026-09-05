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

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        final EditText etNewPassword = findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        TextView txtBackToLogin = findViewById(R.id.txtBackToLogin);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                boolean isValid = true;

                if (newPassword.isEmpty()) {
                    etNewPassword.setError("New password is required");
                    etNewPassword.requestFocus();
                    isValid = false;
                } else if (!isValidPassword(newPassword)) {
                    etNewPassword.setError("Password must be at least 5 characters long and contain at least 1 symbol and 2 integers (e.g. Monday#00)");
                    etNewPassword.requestFocus();
                    isValid = false;
                }

                if (confirmPassword.isEmpty()) {
                    etConfirmPassword.setError("Please confirm your password");
                    if (isValid) {
                        etConfirmPassword.requestFocus();
                    }
                    isValid = false;
                } else if (!newPassword.equals(confirmPassword)) {
                    etConfirmPassword.setError("Passwords do not match");
                    if (isValid) {
                        etConfirmPassword.requestFocus();
                    }
                    isValid = false;
                }

                if (isValid) {
                    // Update Password in Room Database using Hashing Algorithm
                    UserDao userDao = AppDatabase.getInstance(ResetPasswordActivity.this).userDao();
                    String savedParentName = prefs.getString("PARENT_NAME", "");
                    User user = null;
                    if (!savedParentName.isEmpty()) {
                        user = userDao.getUserByName(savedParentName);
                    }
                    if (user == null) {
                        user = userDao.getFirstUser();
                    }

                    String newSalt = PasswordUtils.generateSalt();
                    String newHash = PasswordUtils.hashPassword(newPassword, newSalt);

                    if (user != null) {
                        user.setPasswordHash(newHash);
                        user.setSalt(newSalt);
                        userDao.updateUser(user);
                    } else {
                        // Create default user in Room DB if none existed
                        user = new User("Guardian", "9876543210", "Parent",
                                "Child", "10", "0417",
                                newHash, newSalt);
                        userDao.insertUser(user);
                    }

                    Toast.makeText(ResetPasswordActivity.this, "Password Updated Successfully! Please Log In.", Toast.LENGTH_SHORT).show();

                    // Navigate back to LoginActivity
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 5) {
            return false;
        }

        int digitCount = 0;
        int symbolCount = 0;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
            } else if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                symbolCount++;
            }
        }

        return digitCount >= 2 && symbolCount >= 1;
    }
}
