package com.example.comicwave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicwave.repositories.UserRepository;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    TextView loginSignUpButton;
    EditText loginEmailField, loginPasswordField;
    Button loginLoginButton, loginGoogleButton;
    FrameLayout signInProgressBar;
    private static final int REQ_ONE_TAP = 100;
    private void initComponents() {
        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        loginEmailField = findViewById(R.id.loginEmailField);
        loginPasswordField = findViewById(R.id.loginPasswordField);
        loginLoginButton = findViewById(R.id.loginLoginButton);
        loginGoogleButton = findViewById(R.id.loginGoogleButton);
        signInProgressBar = findViewById(R.id.signInProgressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            Log.d("GoogleSignIn", "Result received");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d("GoogleSignIn", "Account retrieved: " + account.getEmail());
                    Log.d("GoogleSignIn", "User Name: " + account.getDisplayName());
                    UserRepository.firebaseAuthWithGoogle(account.getIdToken(), account.getDisplayName(), callback -> {
                        if (callback) {
                            signInProgressBar.setVisibility(View.GONE);
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                } else {
                    Log.e("GoogleSignIn", "Account is null");
                }
            } catch (ApiException e) {
                Log.e("GoogleSignIn", "ApiException: " + e.getStatusCode() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
//    private void firebaseAuthWithGoogle(String idToken, String displayName) {
//        Log.d("FirebaseAuth", "Starting Firebase authentication");
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Log.d("FirebaseAuth", "Sign-in successful");
//                    } else {
//                        Log.e("FirebaseAuth", "Sign-in failed: " + task.getException().getMessage());
//                        loginGoogleButton.setError("Google sign-in failed");
//                    }
//                });
//    }
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

        initComponents();

        loginGoogleButton.setOnClickListener(v -> {
            signInProgressBar.setVisibility(View.VISIBLE);
            Intent signInIntent = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            ).getSignInIntent();
            startActivityForResult(signInIntent, REQ_ONE_TAP);
        });

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
            signInProgressBar.setVisibility(View.VISIBLE);
            loginLoginButton.setEnabled(false);
            loginLoginButton.setAlpha(0.5f);
            UserRepository.login(email, password, task -> {
                loginLoginButton.setEnabled(true);
                loginLoginButton.setAlpha(1f);
                signInProgressBar.setVisibility(View.GONE);
                if (task) {
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    signInProgressBar.setVisibility(View.GONE);
                    loginPasswordField.setError("Credentials don't match");
                }
            });
        });
    }
}