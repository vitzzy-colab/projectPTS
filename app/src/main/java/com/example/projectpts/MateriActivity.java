package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MateriActivity extends AppCompatActivity {

    private LinearLayout materiContainer;
    private String grade, mapel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);

        materiContainer = findViewById(R.id.materiContainer);

        grade = getIntent().getStringExtra("grade");
        mapel = getIntent().getStringExtra("mapel");

        // Judul tetap ada
        TextView title = findViewById(R.id.txtChoseMateri);
        title.setText("Choose Materi - " + mapel);

        // Ambil daftar materi dari Firebase
        DatabaseReference materiRef = FirebaseDatabase.getInstance()
                .getReference("questions")
                .child(grade)
                .child(mapel);

        materiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ❌ jangan removeAllViews() → biar txtChoseMateri tidak hilang

                for (DataSnapshot materiSnap : snapshot.getChildren()) {
                    String materiName = materiSnap.getKey();

                    TextView tv = new TextView(MateriActivity.this);
                    tv.setText(materiName);
                    tv.setTextSize(24);
                    tv.setTextColor(getResources().getColor(android.R.color.white));
                    tv.setBackgroundResource(R.drawable.rounded_background);
                    tv.setGravity(Gravity.CENTER);
                    tv.setPadding(20, 40, 20, 40);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 20, 0, 0);
                    tv.setLayoutParams(params);

                    tv.setOnClickListener(v -> {
                        Intent i = new Intent(MateriActivity.this, QuizActivity.class);
                        i.putExtra("grade", grade);
                        i.putExtra("mapel", mapel);
                        i.putExtra("materi", materiName);
                        startActivity(i);
                    });

                    materiContainer.addView(tv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
