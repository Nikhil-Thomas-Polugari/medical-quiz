package com.medicalquiz.DTO;

public class AnswerResponseDTO {
    private boolean correct;
    private String correctAnswer;
    private String message;
    
    // Constructor
    public AnswerResponseDTO(boolean correct, String correctAnswer, String message) {
        this.correct = correct;
        this.correctAnswer = correctAnswer;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}