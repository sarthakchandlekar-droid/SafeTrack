package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etParentName, etParentPhone, etParentAge, etParentRelation, etParentPassword;
    private EditText etChildName, etChildAge, etChildDevice;
    private Spinner spParentGender, spChildGender;
    private Button btnRegister;
    private TextView txtLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register);

        // Bind Parent views
        etParentName = findViewById(R.id.etParentName);
        etParentPhone = findViewById(R.id.etParentPhone);
        etParentAge = findViewById(R.id.etParentAge);
        etParentRelation = findViewById(R.id.etParentRelation);
        etParentPassword = findViewById(R.id.etParentPassword);
        spParentGender = findViewById(R.id.spParentGender);

        // Bind Child views
        etChildName = findViewById(R.id.etChildName);
        etChildAge = findViewById(R.id.etChildAge);
        etChildDevice = findViewById(R.id.etChildDevice);
        spChildGender = findViewById(R.id.spChildGender);

        btnRegister = findViewById(R.id.btnRegister);
        txtLoginLink = findViewById(R.id.txtLoginLink);

        // Setup gender dropdown options
        String[] genderOptions = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderOptions);
        spParentGender.setAdapter(adapter);
        spChildGender.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    String parentName = etParentName.getText().toString().trim();
                    String parentPhone = etParentPhone.getText().toString().trim();
                    String parentRelation = etParentRelation.getText().toString().trim();
                    String parentPassword = etParentPassword.getText().toString();
                    String childName = etChildName.getText().toString().trim();
                    String childAge = etChildAge.getText().toString().trim();
                    String childDevice = etChildDevice.getText().toString().trim();

                    // Generate Salt and Password Hash using Password Hashing Algorithm
                    String salt = PasswordUtils.generateSalt();
                    String passwordHash = PasswordUtils.hashPassword(parentPassword, salt);

                    // Insert User into Room Database
                    User user = new User(parentName, parentPhone, parentRelation,
                            childName, childAge, childDevice,
                            passwordHash, salt);
                    AppDatabase.getInstance(RegisterActivity.this).userDao().insertUser(user);

                    // Save registered details into SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("PARENT_NAME", parentName);
                    editor.putString("PARENT_PHONE", parentPhone);
                    editor.putString("PARENT_RELATION", parentRelation);
                    editor.putString("CHILD_NAME", childName);
                    editor.putString("CHILD_AGE", childAge);
                    editor.putString("CHILD_DEVICE", childDevice);
                    editor.apply();

                    Toast.makeText(RegisterActivity.this, "Registration Successful! Please Log In.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("REGISTERED_USER", parentName);
                    intent.putExtra("CHILD_NAME", childName);
                    intent.putExtra("CHILD_RELATION", parentRelation);
                    intent.putExtra("CHILD_DEVICE", childDevice);
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        String parentName = etParentName.getText().toString().trim();
        String parentPhone = etParentPhone.getText().toString().trim();
        String parentAgeStr = etParentAge.getText().toString().trim();
        String parentRelation = etParentRelation.getText().toString().trim();
        String parentPassword = etParentPassword.getText().toString();

        String childName = etChildName.getText().toString().trim();
        String childAgeStr = etChildAge.getText().toString().trim();

        // 1. Parent Name
        if (parentName.isEmpty()) {
            etParentName.setError("Parent name is required");
            etParentName.requestFocus();
            isValid = false;
        } else if (!containsLetters(parentName)) {
            etParentName.setError("Parent name must contain letters");
            etParentName.requestFocus();
            isValid = false;
        }

        // 2. Parent Phone
        if (parentPhone.isEmpty()) {
            etParentPhone.setError("Phone number is required");
            if (isValid) etParentPhone.requestFocus();
            isValid = false;
        } else if (parentPhone.length() < 10) {
            etParentPhone.setError("Enter a valid phone number (min 10 digits)");
            if (isValid) etParentPhone.requestFocus();
            isValid = false;
        }

        // 3. Parent Age
        if (parentAgeStr.isEmpty()) {
            etParentAge.setError("Parent age is required");
            if (isValid) etParentAge.requestFocus();
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(parentAgeStr);
                if (age < 18 || age > 120) {
                    etParentAge.setError("Enter a valid parent age (18+)");
                    if (isValid) etParentAge.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etParentAge.setError("Enter a valid number for age");
                if (isValid) etParentAge.requestFocus();
                isValid = false;
            }
        }

        // 4. Parent Gender
        if (spParentGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select Parent Gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // 5. Parent Relation
        if (parentRelation.isEmpty()) {
            etParentRelation.setError("Relation to child is required");
            if (isValid) etParentRelation.requestFocus();
            isValid = false;
        } else if (!containsLetters(parentRelation)) {
            etParentRelation.setError("Relation must contain letters (e.g. Father)");
            if (isValid) etParentRelation.requestFocus();
            isValid = false;
        }

        // 6. Parent Password
        if (parentPassword.isEmpty()) {
            etParentPassword.setError("Password is required");
            if (isValid) etParentPassword.requestFocus();
            isValid = false;
        } else if (!isValidPassword(parentPassword)) {
            etParentPassword.setError("Password must be min 5 chars, contain at least 1 symbol and 2 digits");
            if (isValid) etParentPassword.requestFocus();
            isValid = false;
        }

        // 7. Child Name
        if (childName.isEmpty()) {
            etChildName.setError("Child name is required");
            if (isValid) etChildName.requestFocus();
            isValid = false;
        } else if (!containsLetters(childName)) {
            etChildName.setError("Child name must contain letters");
            if (isValid) etChildName.requestFocus();
            isValid = false;
        }

        // 8. Child Age
        if (childAgeStr.isEmpty()) {
            etChildAge.setError("Child age is required");
            if (isValid) etChildAge.requestFocus();
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(childAgeStr);
                if (age <= 0 || age >= 18) {
                    etChildAge.setError("Enter a valid child age (1 - 17)");
                    if (isValid) etChildAge.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etChildAge.setError("Enter a valid number for age");
                if (isValid) etChildAge.requestFocus();
                isValid = false;
            }
        }

        // 9. Child Gender
        if (spChildGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select Child Gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean containsLetters(String text) {
        return text.matches(".*[a-zA-Z].*");
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
