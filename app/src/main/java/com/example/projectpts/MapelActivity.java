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

public class MapelActivity extends AppCompatActivity {

    private LinearLayout mapelContainer;
    private String grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapel);

        mapelContainer = findViewById(R.id.mapelContainer);
        grade = getIntent().getStringExtra("grade");

        // Judul tetap ada
        TextView title = findViewById(R.id.txtChoseMapel);
        title.setText("Choose Mapel - Kelas " + grade);

        DatabaseReference mapelRef = FirebaseDatabase.getInstance()
                .getReference("questions")
                .child(grade);

        mapelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ❌ jangan removeAllViews() → biar txtChoseMapel tidak hilang

                for (DataSnapshot mapelSnap : snapshot.getChildren()) {
                    String mapelName = mapelSnap.getKey();

                    TextView tv = new TextView(MapelActivity.this);
                    tv.setText(mapelName);
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
                        Intent i = new Intent(MapelActivity.this, MateriActivity.class);
                        i.putExtra("grade", grade);
                        i.putExtra("mapel", mapelName);
                        startActivity(i);
                    });

                    mapelContainer.addView(tv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
