package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView txtQuestion, txtProgress, txtScore;
    private Button btnA, btnB, btnC, btnD, btnKembali, btnSkip;
    private ImageView showLevel;
    private ScrollView scrollQuiz;

    private List<Question> selectedQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private ArrayList<String> answers = new ArrayList<>();

    private String grade, mapel, materi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        scrollQuiz = findViewById(R.id.scrollQuiz);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtProgress = findViewById(R.id.txtProgress);
        txtScore = findViewById(R.id.txtScore);
        showLevel = findViewById(R.id.showlevel);

        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
        btnKembali = findViewById(R.id.btnKEMBALI);
        btnSkip = findViewById(R.id.btnSKIP);

        grade = getIntent().getStringExtra("grade");
        mapel = getIntent().getStringExtra("mapel");
        materi = getIntent().getStringExtra("materi");

        loadQuestionsFromFirebase();

        btnKembali.setOnClickListener(v -> finish());

        btnSkip.setOnClickListener(v -> {
            Question q = selectedQuestions.get(currentQuestionIndex);
            answers.add("Tidak dijawab: " + q.getQuestion());
            nextQuestion();
        });
    }

    private void loadQuestionsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions")
                .child(grade)
                .child(mapel)
                .child(materi);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> level1 = new ArrayList<>();
                List<Question> level2 = new ArrayList<>();
                List<Question> level3 = new ArrayList<>();

                for (DataSnapshot levelSnap : snapshot.getChildren()) {
                    int level = Integer.parseInt(levelSnap.getKey());

                    for (DataSnapshot qSnap : levelSnap.getChildren()) {
                        String question = qSnap.child("question").getValue(String.class);
                        String optionA = qSnap.child("optionA").getValue(String.class);
                        String optionB = qSnap.child("optionB").getValue(String.class);
                        String optionC = qSnap.child("optionC").getValue(String.class);
                        String optionD = qSnap.child("optionD").getValue(String.class);
                        String correctAnswer = qSnap.child("correctAnswer").getValue(String.class);

                        Question q = new Question(question, optionA, optionB, optionC, optionD, correctAnswer, level);

                        if (level == 1) level1.add(q);
                        else if (level == 2) level2.add(q);
                        else if (level == 3) level3.add(q);
                    }
                }

                Collections.shuffle(level1);
                Collections.shuffle(level2);
                Collections.shuffle(level3);

                selectedQuestions.addAll(level1.subList(0, Math.min(10, level1.size())));
                selectedQuestions.addAll(level2.subList(0, Math.min(6, level2.size())));
                selectedQuestions.addAll(level3.subList(0, Math.min(4, level3.size())));

                if (!selectedQuestions.isEmpty()) {
                    showQuestion();
                } else {
                    Toast.makeText(QuizActivity.this, "Soal tidak tersedia", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizActivity.this, "Gagal ambil soal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestion() {
        Question q = selectedQuestions.get(currentQuestionIndex);

        txtQuestion.setText(q.getQuestion());
        btnA.setText("A. " + q.getOptionA());
        btnB.setText("B. " + q.getOptionB());
        btnC.setText("C. " + q.getOptionC());
        btnD.setText("D. " + q.getOptionD());

        txtProgress.setText("Soal " + (currentQuestionIndex + 1) + " dari " + selectedQuestions.size());
        txtScore.setText("Score: " + score);

        switch (q.getLevel()) {
            case 1: showLevel.setImageResource(R.drawable.level_1); break;
            case 2: showLevel.setImageResource(R.drawable.level_2); break;
            case 3: showLevel.setImageResource(R.drawable.level_3); break;
        }

        scrollQuiz.fullScroll(ScrollView.FOCUS_UP);

        btnA.setOnClickListener(v -> checkAnswer("A"));
        btnB.setOnClickListener(v -> checkAnswer("B"));
        btnC.setOnClickListener(v -> checkAnswer("C"));
        btnD.setOnClickListener(v -> checkAnswer("D"));
    }

    private void checkAnswer(String answer) {
        Question q = selectedQuestions.get(currentQuestionIndex);

        int point = 0;
        switch (q.getLevel()) {
            case 1: point = 2; break;
            case 2: point = 3; break;
            case 3: point = 5; break;
        }

        if (answer.equals(q.getCorrectAnswer())) {
            score += point;
            answers.add("Benar: " + q.getQuestion() + " (+" + point + " pts)");
        } else {
            answers.add("Salah: " + q.getQuestion() + " (Jawaban benar: " + q.getCorrectAnswer() + ")");
        }

        nextQuestion();
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < selectedQuestions.size()) {
            showQuestion();
        } else {
            Toast.makeText(this, "Kuis selesai! Skor akhir: " + score, Toast.LENGTH_LONG).show();

            // Pindah ke ScoreActivity
            Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("answers", answers);
            intent.putExtra("grade", grade);
            intent.putExtra("mapel", mapel);
            intent.putExtra("materi", materi);
            startActivity(intent);

            finish(); // Tutup QuizActivity biar nggak bisa balik ke sini
        }
    }
}
