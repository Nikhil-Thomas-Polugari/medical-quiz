package com.medicalquiz.DTO;

public class QuestionDTO {
    private Long questionId;
    private String questionText;
    private String difficulty;
    private String category;
    
    // Constructor
    public QuestionDTO(Long questionId, String questionText, String difficulty, String category) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}