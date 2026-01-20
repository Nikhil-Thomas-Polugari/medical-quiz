package com.medicalquiz.DTO;

public class AnswerSubmissionDTO {
    private Long questionId;
    private String answer;
    
    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}