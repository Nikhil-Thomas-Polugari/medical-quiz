package com.medicalquiz.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private String question;   // The question text
    private String answer;     // The correct answer
    private Integer answered;  // Number of times answered or answer status
}