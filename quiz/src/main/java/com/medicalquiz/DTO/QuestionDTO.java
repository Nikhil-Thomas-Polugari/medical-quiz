package com.medicalquiz.DTO;

public class QuestionDTO {
    private String question;
    private String answer;
    
    // Constructor - only includes fields that should be sent to client
    public QuestionDTO(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    
    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}