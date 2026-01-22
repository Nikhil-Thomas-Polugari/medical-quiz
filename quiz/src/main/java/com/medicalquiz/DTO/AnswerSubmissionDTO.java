package com.medicalquiz.DTO;

public class AnswerSubmissionDTO {
    private String question;  // The question text to identify which question
    private String answer;    // User's answer
    
    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}