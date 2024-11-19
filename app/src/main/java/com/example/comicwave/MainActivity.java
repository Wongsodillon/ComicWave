package com.example.comicwave;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicwave.repositories.UserRepository;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private void isAuthenticated() {
        UserRepository.getCurrentUser(user -> {
            Intent i;
            if (user != null) {
                i = new Intent(MainActivity.this, HomeActivity.class);
            }
            else {
                i = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(i);
            finish();
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(this::isAuthenticated, 500);
    }
}