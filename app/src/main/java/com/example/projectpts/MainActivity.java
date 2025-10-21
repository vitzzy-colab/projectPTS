package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MainActivity extends AppCompatActivity {

    Button btnPlay, btnInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnplay);
        btnInput = findViewById(R.id.btnInput);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
            }
        });

        btnInput.setOnClickListener(v -> {
            // 🔹 Popup login admin
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Login Admin 🔐");

            // Layout vertikal untuk email + password
            android.widget.LinearLayout layout = new android.widget.LinearLayout(MainActivity.this);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(50, 20, 50, 10);

            final android.widget.EditText inputEmail = new android.widget.EditText(MainActivity.this);
            inputEmail.setHint("Email admin");
            inputEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            layout.addView(inputEmail);

            final android.widget.EditText inputPassword = new android.widget.EditText(MainActivity.this);
            inputPassword.setHint("Password");
            inputPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(inputPassword);

            builder.setView(layout);

            builder.setPositiveButton("Login", (dialog, which) -> {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    android.widget.Toast.makeText(MainActivity.this, "Isi email dan password dulu ya 💡", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔹 Proses login pakai FirebaseAuth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            // Cek apakah email admin terdaftar di database
                            DatabaseReference adminRef = FirebaseDatabase.getInstance()
                                    .getReference("admins")
                                    .child(email.replace(".", "_"));

                            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // ✅ Admin valid → buka halaman input soal
                                        Intent intent = new Intent(MainActivity.this, AddQuestionActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Akun ini bukan admin ❌", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Login gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

            builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }
}