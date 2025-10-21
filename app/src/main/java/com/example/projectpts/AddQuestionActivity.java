package com.example.projectpts;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddQuestionActivity extends AppCompatActivity {

    private Spinner spGrade, spMapel, spMateri, spLevel;
    private EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etAnswer;
    private Button btnSubmit, btnAddGrade, btnAddMapel, btnAddMateri, btnAddLevel, btnViewQuestions, btnPreviewQuestion;
    private DatabaseReference dbRef;

    private ArrayAdapter<String> adapterGrade, adapterMapel, adapterMateri, adapterLevel;
    private String selectedGrade, selectedMapel, selectedMateri, selectedLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        dbRef = FirebaseDatabase.getInstance().getReference("questions");

        // Spinner
        spGrade = findViewById(R.id.spGrade);
        spMapel = findViewById(R.id.spMapel);
        spMateri = findViewById(R.id.spMateri);
        spLevel = findViewById(R.id.spLevel);

        // Buttons
        btnAddGrade = findViewById(R.id.btnAddGrade);
        btnAddMapel = findViewById(R.id.btnAddMapel);
        btnAddMateri = findViewById(R.id.btnAddMateri);
        btnAddLevel = findViewById(R.id.btnAddLevel);
        btnViewQuestions = findViewById(R.id.btnViewQuestions);
        btnPreviewQuestion = findViewById(R.id.btnPreviewQuestion);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Inputs
        etQuestion = findViewById(R.id.etQuestion);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        etAnswer = findViewById(R.id.etAnswer);

        loadGrades();

        // Spinner listeners
        spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = (String) spGrade.getSelectedItem();
                loadMapel(selectedGrade);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spMapel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMapel = (String) spMapel.getSelectedItem();
                loadMateri(selectedGrade, selectedMapel);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spMateri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMateri = (String) spMateri.getSelectedItem();
                loadLevels(selectedGrade, selectedMapel, selectedMateri);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLevel = (String) spLevel.getSelectedItem();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnPreviewQuestion.setOnClickListener(v -> showQuestionPreview());
        btnAddGrade.setOnClickListener(v -> addEntityDialog("grade", dbRef));

        btnAddMapel.setOnClickListener(v -> {
            if (selectedGrade == null) {
                Toast.makeText(this, "Pilih kelas dulu 💡", Toast.LENGTH_SHORT).show();
                return;
            }
            addEntityDialog("mapel", dbRef.child(selectedGrade));
        });

        btnAddMateri.setOnClickListener(v -> {
            if (selectedGrade == null || selectedMapel == null) {
                Toast.makeText(this, "Pilih kelas & mapel dulu 💡", Toast.LENGTH_SHORT).show();
                return;
            }
            addEntityDialog("materi", dbRef.child(selectedGrade).child(selectedMapel));
        });

        btnAddLevel.setOnClickListener(v -> {
            if (selectedGrade == null || selectedMapel == null || selectedMateri == null) {
                Toast.makeText(this, "Lengkapi kelas, mapel, dan materi 💡", Toast.LENGTH_SHORT).show();
                return;
            }
            addEntityDialog("level", dbRef.child(selectedGrade).child(selectedMapel).child(selectedMateri));
        });

        btnViewQuestions.setOnClickListener(v -> showQuestionListDialog());
        btnSubmit.setOnClickListener(v -> saveQuestion());
    }

    private void showQuestionPreview() {
        String qText = etQuestion.getText().toString().trim();
        String a = etOptionA.getText().toString().trim();
        String b = etOptionB.getText().toString().trim();
        String c = etOptionC.getText().toString().trim();
        String d = etOptionD.getText().toString().trim();
        String correct = etAnswer.getText().toString().trim();

        if (qText.isEmpty()) {
            Toast.makeText(this, "Isi pertanyaan dulu untuk preview!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Preview Soal");

        WebView previewWebView = new WebView(this);
        WebSettings webSettings = previewWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String htmlPreview = "<html><head>" +
                "<script type=\"text/javascript\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js\"></script>" +
                "</head><body style='font-size:18px; padding:8px;'>" +
                "<b>Pertanyaan:</b><br>" + qText + "<br><br>" +
                "<b>Opsi A:</b> " + a + "<br>" +
                "<b>Opsi B:</b> " + b + "<br>" +
                "<b>Opsi C:</b> " + c + "<br>" +
                "<b>Opsi D:</b> " + d + "<br><br>" +
                "<b>Jawaban Benar:</b> " + correct +
                "</body></html>";
        previewWebView.loadDataWithBaseURL(null, htmlPreview, "text/html", "utf-8", null);

        builder.setView(previewWebView);
        builder.setNegativeButton("Tutup", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addEntityDialog(String entityName, DatabaseReference parentRef) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah " + entityName);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (value.isEmpty()) {
                Toast.makeText(this, entityName + " tidak boleh kosong ❌", Toast.LENGTH_SHORT).show();
                return;
            }
            parentRef.child(value).setValue("")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, entityName + " berhasil ditambahkan ✅", Toast.LENGTH_SHORT).show();
                        switch (entityName) {
                            case "grade": loadGrades(); break;
                            case "mapel": loadMapel(selectedGrade); break;
                            case "materi": loadMateri(selectedGrade, selectedMapel); break;
                            case "level": loadLevels(selectedGrade, selectedMapel, selectedMateri); break;
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal menambahkan " + entityName, Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void loadGrades() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> grades = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) grades.add(child.getKey());
                adapterGrade = new ArrayAdapter<>(AddQuestionActivity.this, android.R.layout.simple_spinner_dropdown_item, grades);
                spGrade.setAdapter(adapterGrade);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadMapel(String grade) {
        if (grade == null) return;
        dbRef.child(grade).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) list.add(child.getKey());
                adapterMapel = new ArrayAdapter<>(AddQuestionActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                spMapel.setAdapter(adapterMapel);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadMateri(String grade, String mapel) {
        if (grade == null || mapel == null) return;
        dbRef.child(grade).child(mapel).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) list.add(child.getKey());
                adapterMateri = new ArrayAdapter<>(AddQuestionActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                spMateri.setAdapter(adapterMateri);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadLevels(String grade, String mapel, String materi) {
        if (grade == null || mapel == null || materi == null) return;
        dbRef.child(grade).child(mapel).child(materi).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) list.add(child.getKey());
                adapterLevel = new ArrayAdapter<>(AddQuestionActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                spLevel.setAdapter(adapterLevel);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showQuestionListDialog() {
        if (selectedGrade == null || selectedMapel == null || selectedMateri == null || selectedLevel == null) {
            Toast.makeText(this, "Lengkapi grade, mapel, materi, level!", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference ref = dbRef.child(selectedGrade).child(selectedMapel).child(selectedMateri).child(selectedLevel);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> items = new ArrayList<>();
                final ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    String q = child.child("question").getValue(String.class);
                    items.add(key + ": " + q);
                    keys.add(key);
                }
                if (items.isEmpty()) {
                    Toast.makeText(AddQuestionActivity.this, "Belum ada soal pada level ini.", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AddQuestionActivity.this);
                builder.setTitle("Daftar Soal:");
                builder.setItems(items.toArray(new String[0]), (dialog, which) -> {
                    String key = keys.get(which);
                    DataSnapshot soal = snapshot.child(key);
                    editOrDeleteQuestion(ref, key, soal);
                });
                builder.setNegativeButton("Tutup", (d, w) -> d.dismiss());
                builder.show();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddQuestionActivity.this, "Gagal mengambil soal!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editOrDeleteQuestion(DatabaseReference ref, String key, DataSnapshot soal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit/Hapus Soal");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText editText = new EditText(this);
        editText.setText(soal.child("question").getValue(String.class));
        layout.addView(editText);

        builder.setView(layout);

        builder.setPositiveButton("Simpan Edit", (dialog, which) -> {
            String newQ = editText.getText().toString().trim();
            ref.child(key).child("question").setValue(newQ)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Soal diupdate!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal update!", Toast.LENGTH_SHORT).show());
        });
        builder.setNeutralButton("Hapus Soal", (dialog, which) -> {
            ref.child(key).removeValue()
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Soal dihapus!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal hapus!", Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveQuestion() {
        if (selectedGrade == null || selectedMapel == null || selectedMateri == null || selectedLevel == null) {
            Toast.makeText(this, "Lengkapi dulu grade, mapel, materi, dan level 💕", Toast.LENGTH_SHORT).show();
            return;
        }
        String question = etQuestion.getText().toString().trim();
        String a = etOptionA.getText().toString().trim();
        String b = etOptionB.getText().toString().trim();
        String c = etOptionC.getText().toString().trim();
        String d = etOptionD.getText().toString().trim();
        String correct = etAnswer.getText().toString().trim();

        if (question.isEmpty() || correct.isEmpty()) {
            Toast.makeText(this, "Isi semua data soal ya 💞", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = dbRef.child(selectedGrade).child(selectedMapel).child(selectedMateri).child(selectedLevel);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                String qKey = "q" + (count + 1);

                Map<String, Object> data = new HashMap<>();
                data.put("question", question);
                data.put("optionA", a);
                data.put("optionB", b);
                data.put("optionC", c);
                data.put("optionD", d);
                data.put("correctAnswer", correct.toUpperCase());

                ref.child(qKey).setValue(data)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(AddQuestionActivity.this, "Soal berhasil disimpan!", Toast.LENGTH_SHORT).show();
                            etQuestion.setText("");
                            etOptionA.setText("");
                            etOptionB.setText("");
                            etOptionC.setText("");
                            etOptionD.setText("");
                            etAnswer.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(AddQuestionActivity.this, "Gagal simpan soal", Toast.LENGTH_SHORT).show());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddQuestionActivity.this, "Gagal memproses permintaan ❌", Toast.LENGTH_SHORT).show();
            }
        });
    }
}