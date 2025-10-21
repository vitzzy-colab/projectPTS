package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GradeActivity extends AppCompatActivity {

    private LinearLayout gradeElementaryLayout, gradeJuniorLayout, gradeSeniorLayout;
    private TextView btnElementary, btnJunior, btnSenior;
    private TextView btnGrade4, btnGrade5, btnGrade6,
            btnGrade7, btnGrade8, btnGrade9,
            btnGrade10, btnGrade11, btnGrade12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        // Ambil id
        gradeElementaryLayout = findViewById(R.id.gradeElementaryLayout);
        gradeJuniorLayout = findViewById(R.id.gradeJuniorLayout);
        gradeSeniorLayout = findViewById(R.id.gradeSeniorLayout);

        btnElementary = findViewById(R.id.btnElementary);
        btnJunior = findViewById(R.id.btnJunior);
        btnSenior = findViewById(R.id.btnSenior);

        btnGrade4 = findViewById(R.id.btnGrade4);
        btnGrade5 = findViewById(R.id.btnGrade5);
        btnGrade6 = findViewById(R.id.btnGrade6);

        btnGrade7 = findViewById(R.id.btnGrade7);
        btnGrade8 = findViewById(R.id.btnGrade8);
        btnGrade9 = findViewById(R.id.btnGrade9);

        btnGrade10 = findViewById(R.id.btnGrade10);
        btnGrade11 = findViewById(R.id.btnGrade11);
        btnGrade12 = findViewById(R.id.btnGrade12);

        // Accordion toggle
        btnElementary.setOnClickListener(v -> toggleAccordion(gradeElementaryLayout));
        btnJunior.setOnClickListener(v -> toggleAccordion(gradeJuniorLayout));
        btnSenior.setOnClickListener(v -> toggleAccordion(gradeSeniorLayout));

        // Klik grade → buka MapelActivity
        btnGrade4.setOnClickListener(v -> openMapel("4"));
        btnGrade5.setOnClickListener(v -> openMapel("5"));
        btnGrade6.setOnClickListener(v -> openMapel("6"));

        btnGrade7.setOnClickListener(v -> openMapel("7"));
        btnGrade8.setOnClickListener(v -> openMapel("8"));
        btnGrade9.setOnClickListener(v -> openMapel("9"));

        btnGrade10.setOnClickListener(v -> openMapel("10"));
        btnGrade11.setOnClickListener(v -> openMapel("11"));
        btnGrade12.setOnClickListener(v -> openMapel("12"));
    }

    private void toggleAccordion(LinearLayout targetLayout) {
        // Tutup semua dulu
        gradeElementaryLayout.setVisibility(View.GONE);
        gradeJuniorLayout.setVisibility(View.GONE);
        gradeSeniorLayout.setVisibility(View.GONE);

        // Kalau target sebelumnya tertutup → buka
        if (targetLayout.getVisibility() == View.GONE) {
            targetLayout.setVisibility(View.VISIBLE);
        }
    }

    private void openMapel(String grade) {
        Intent i = new Intent(GradeActivity.this, MapelActivity.class);
        i.putExtra("grade", grade);
        startActivity(i);
    }
}
