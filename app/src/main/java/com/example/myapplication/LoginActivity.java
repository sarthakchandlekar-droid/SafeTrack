package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_login);

        final EditText etName = findViewById(R.id.etName);
        Button btnLogin = findViewById(R.id.btnLogin);
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    name = "User"; // Default name if empty
                }
                
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("USER_NAME", name);
                startActivity(intent);
                finish();
            }
        });
    }
}