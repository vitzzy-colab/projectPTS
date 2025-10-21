package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    private TextView txtMateri, txtTotScore;
    private LinearLayout scrlLinear;
    private Button btnKELUAR, btnULANGI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        txtMateri = findViewById(R.id.txtMateri);
        txtTotScore = findViewById(R.id.txtTotScore);
        scrlLinear = findViewById(R.id.scrlLinear);
        btnKELUAR = findViewById(R.id.btnKELUAR);
        btnULANGI = findViewById(R.id.btnULANGI);

        // Ambil data dari Intent
        int totalScore = getIntent().getIntExtra("score", 0);
        String materi = getIntent().getStringExtra("materi");
        ArrayList<String> detailList = getIntent().getStringArrayListExtra("answers");

        // Set tampilan
        txtMateri.setText("Materi: " + (materi != null ? materi : "-"));
        txtTotScore.setText("SCORE: " + totalScore);

        if (detailList != null && !detailList.isEmpty()) {
            for (String detail : detailList) {
                TextView tv = new TextView(this);
                tv.setText(detail);
                tv.setTextColor(getResources().getColor(android.R.color.white));
                tv.setTextSize(18);
                tv.setPadding(0, 5, 0, 5);
                scrlLinear.addView(tv);
            }
        } else {
            TextView tv = new TextView(this);
            tv.setText("Tidak ada detail jawaban");
            tv.setTextColor(getResources().getColor(android.R.color.white));
            tv.setTextSize(18);
            scrlLinear.addView(tv);
        }

        // Tombol keluar → kembali ke GradeActivity
        btnKELUAR.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, GradeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Tombol ulangi → ulang quiz yang sama
        btnULANGI.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, QuizActivity.class);
            intent.putExtra("grade", getIntent().getStringExtra("grade"));
            intent.putExtra("mapel", getIntent().getStringExtra("mapel"));
            intent.putExtra("materi", materi);
            startActivity(intent);
            finish();
        });
    }
}
