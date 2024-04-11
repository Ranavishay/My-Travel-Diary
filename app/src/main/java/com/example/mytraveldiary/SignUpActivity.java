package com.example.mytraveldiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, password,confirmPassword;
    private Button buttonSignUp;
    private TextView login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        login=findViewById(R.id.buttonLogin);
        mAuth = FirebaseAuth.getInstance();

        buttonSignUp.setOnClickListener(view -> signUp());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();
            }
        });
    }
    // Go to Login Activity
    private void login() {

        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }
    private void signUp() {
        String emailDetails = email.getText().toString().trim();
        String passwordDetails = password.getText().toString().trim();
        String confirmPasswordDetails = confirmPassword.getText().toString().trim();

        if (!emailDetails.isEmpty() && !passwordDetails.isEmpty() && !confirmPasswordDetails.isEmpty()) {
            if (passwordDetails.equals(confirmPasswordDetails)) {
                // Sign up the user with email and password
                mAuth.createUserWithEmailAndPassword(emailDetails, passwordDetails)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    // If the user is created Go to Login Activity
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

}
