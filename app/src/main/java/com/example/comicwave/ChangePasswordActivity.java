package com.example.comicwave;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicwave.fragments.account.AccountFragment;
import com.example.comicwave.repositories.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText accountConfirmPasswordField, accountNewPasswordField, accountOldPasswordField;
    private TextInputLayout accountOldPasswordFieldLayout, accountNewPasswordFieldLayout, accountConfirmPasswordFieldLayout;
    private Button accountLogoutButton;

    private void initComponents() {
        accountOldPasswordFieldLayout = findViewById(R.id.accountOldPasswordFieldLayout);
        accountNewPasswordFieldLayout = findViewById(R.id.accountNewPasswordFieldLayout);
        accountConfirmPasswordFieldLayout = findViewById(R.id.accountConfirmPasswordFieldLayout);

        accountOldPasswordField = findViewById(R.id.accountOldPasswordField);
        accountNewPasswordField = findViewById(R.id.accountNewPasswordField);
        accountConfirmPasswordField = findViewById(R.id.accountConfirmPasswordField);

        accountLogoutButton = findViewById(R.id.accountLogoutButton);
        accountLogoutButton.setOnClickListener(e -> {
            String oldPassword = accountOldPasswordField.getText().toString();
            String newPassword = accountNewPasswordField.getText().toString();
            String confirmPassword = accountConfirmPasswordField.getText().toString();

            accountOldPasswordFieldLayout.setError(null);
            accountNewPasswordFieldLayout.setError(null);
            accountConfirmPasswordFieldLayout.setError(null);

            if (oldPassword.isEmpty()) {
                accountOldPasswordFieldLayout.setError("Old password is required");
                return;
            }

            if (newPassword.isEmpty()) {
                accountNewPasswordFieldLayout.setError("New password is required");
                return;
            }

            if (confirmPassword.isEmpty()) {
                accountConfirmPasswordFieldLayout.setError("Confirm password is required");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                accountConfirmPasswordFieldLayout.setError("Passwords do not match");
                return;
            }

            UserRepository.validateOldPassword(oldPassword, isValid -> {
                if (isValid) {
                    UserRepository.updatePassword(newPassword, isSuccess -> {
                        if (isSuccess) {
                            Toast.makeText(this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            Toast.makeText(this, "Unable to update password, Try again later.", Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    accountOldPasswordFieldLayout.setError("Old password is incorrect");
                }
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
    }
}