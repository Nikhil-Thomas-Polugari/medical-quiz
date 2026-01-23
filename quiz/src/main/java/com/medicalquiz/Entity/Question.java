package com.medicalquiz.Entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Question {
    private String question_text;   // The question text
    private String answer;          // The correct answer
    private Integer answered;       // Number of times answered or answer status

    // Custom getters to match your naming convention
    public String getQuestion() {
        return this.question_text;
    }
    
    public String getAnswer() {
        return this.answer;
    }
    
    public Integer getAnswered() {
        return this.answered;
    }

    // Custom setters to match your naming convention
    public void setQuestion(String question_text) {
        this.question_text = question_text;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public void setAnswered(Integer answered) {
        this.answered = answered;
    }
}