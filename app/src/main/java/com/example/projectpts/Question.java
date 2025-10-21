package com.example.projectpts;

public class Question {
    private String question, optionA, optionB, optionC, optionD, correctAnswer;
    private int level;

    public Question() {}

    public Question(String question, String optionA, String optionB, String optionC,
                    String optionD, String correctAnswer, int level) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.level = level;
    }

    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getLevel() { return level; }
}
