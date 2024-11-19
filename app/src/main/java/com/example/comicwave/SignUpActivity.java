package com.example.comicwave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicwave.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    TextView signUpLoginButton;
    Button signUpSignUpButton;
    EditText signUpFullNameField, signUpEmailField, signUpPasswordField, signUpConfirmPasswordField;
    ProgressBar signUpProgressBar;
    private void initComponents() {
        signUpLoginButton = findViewById(R.id.signUpLoginButton);
        signUpSignUpButton = findViewById(R.id.signUpSignUpButton);
        signUpFullNameField = findViewById(R.id.signUpFullNameField);
        signUpEmailField = findViewById(R.id.signUpEmailField);
        signUpPasswordField = findViewById(R.id.signUpPasswordField);
        signUpConfirmPasswordField = findViewById(R.id.signUpConfirmPasswordField);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
        signUpLoginButton.setOnClickListener(e -> {
            Intent i  = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
        });
        signUpSignUpButton.setOnClickListener(e -> {
            String name, email, password, confirmPassword;
            name = signUpFullNameField.getText().toString().trim();
            email = signUpEmailField.getText().toString().trim();
            password = signUpPasswordField.getText().toString().trim();
            confirmPassword = signUpConfirmPasswordField.getText().toString().trim();
            Log.d("SignUpInputs", "Email: '" + email + "', Password: '" + password + "'");
            boolean flag = false;
            if (name.isEmpty()) {
                signUpFullNameField.setError("Your name can't be empty");
                flag = true;
            }
            else if (name.length() < 3) {
                signUpFullNameField.setError("Name must be at least 3 characters long");
                flag = true;
            }
            if(email.isEmpty()) {
                signUpEmailField.setError("Email cannot be empty!");
                flag = true;
            } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signUpEmailField.setError("Email must be in a correct format!");
                flag = true;
            }

            if(password.isEmpty()) {
                signUpPasswordField.setError("Password cannot be empty");
                flag = true;
            }
            else if(password.length() > 30) {
                signUpPasswordField.setError("Your password must be between 8 and 30 characters");
                flag = true;
            }

            if(!password.equals(confirmPassword)) {
                signUpConfirmPasswordField.setError("Password didn't match!");
                flag = true;
            }
            if (flag) {
                return;
            }

            signUpProgressBar.setVisibility(View.VISIBLE);
            signUpSignUpButton.setEnabled(false);
            signUpSignUpButton.setAlpha(0.5f);
            UserRepository.isUniqueEmail(email, isUnique -> {
                if (!isUnique) {
                    signUpEmailField.setError("Email is already taken!");
                    signUpProgressBar.setVisibility(View.GONE);
                    signUpSignUpButton.setEnabled(true);
                    signUpSignUpButton.setAlpha(1.0f);
                    return;
                }
                UserRepository.signUp(name, email, password, task -> {
                    signUpProgressBar.setVisibility(View.GONE);
                    signUpSignUpButton.setEnabled(true);
                    signUpSignUpButton.setAlpha(1.0f);
                    if (!task) {
                        signUpEmailField.setError("Error Signing Up");
                        return;
                    }
                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                });
            });
        });
    }
}