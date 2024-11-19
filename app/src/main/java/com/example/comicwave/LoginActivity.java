package com.example.comicwave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicwave.repositories.UserRepository;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginActivity extends AppCompatActivity {

    TextView loginSignUpButton;
    EditText loginEmailField, loginPasswordField;
    Button loginLoginButton, loginGoogleButton;
    private GoogleSignInClient googleSignInClient;
    private void initComponents() {
        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        loginEmailField = findViewById(R.id.loginEmailField);
        loginPasswordField = findViewById(R.id.loginPasswordField);
        loginLoginButton = findViewById(R.id.loginLoginButton);
        loginGoogleButton = findViewById(R.id.loginGoogleButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        Button googleSignInButton = findViewById(R.id.loginGoogleButton);

        initComponents();
        loginSignUpButton.setOnClickListener(e -> {
            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);
        });
        loginLoginButton.setOnClickListener(e -> {
            String email = loginEmailField.getText().toString();
            String password = loginPasswordField.getText().toString();

            Boolean flag = false;
            if (email.isEmpty()) {
                loginEmailField.setError("Email can't be empty");
                flag = true;
            }
            if (password.isEmpty()) {
                loginPasswordField.setError("Password can't be empty");
                flag = true;
            }
            if (flag) {
                return;
            }
            loginLoginButton.setEnabled(false);
            loginLoginButton.setAlpha(0.5f);
            UserRepository.login(email, password, task -> {
                loginLoginButton.setEnabled(true);
                loginLoginButton.setAlpha(1f);
                if (task) {
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    loginPasswordField.setError("Credentials don't match");
                }
            });
        });
    }

}