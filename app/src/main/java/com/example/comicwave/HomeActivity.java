package com.example.comicwave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicwave.repositories.UserRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private void initComponents() {
//    DocumentReference sourceDocRef = db.collection("comic").document("TiX6DUlwWe8d9tzwvnfW");
//        sourceDocRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                Map<String, Object> documentData = task.getResult().getData();
//                if (documentData != null) {
//                    for (int i = 0; i < 1; i++) {
//                        db.collection("comic").add(documentData).addOnCompleteListener(addTask -> {
//                            if (addTask.isSuccessful()) {
//                                String newDocId = addTask.getResult().getId();
//                                Log.d("New ID", newDocId);
//                            } else {
//                                Log.d("New ID", "Failed to duplicate document " + task.getResult().getId());
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
    }
}