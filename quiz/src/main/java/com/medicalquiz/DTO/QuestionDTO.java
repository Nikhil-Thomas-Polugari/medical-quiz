package com.medicalquiz.DTO;

public class QuestionDTO {
    private String question;
    private Integer answered;
    
    // Constructor - only includes fields that should be sent to client
    public QuestionDTO(String question, Integer answered) {
        this.question = question;
        this.answered = answered;
    }
    
    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public Integer getAnswered() { return answered; }
    public void setAnswered(Integer answered) { this.answered = answered; }
}