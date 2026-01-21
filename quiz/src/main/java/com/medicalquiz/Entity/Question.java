package com.medicalquiz.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long questionId;
    private String questionText;
    private String correctAnswer;  // Just the answer text
    private String difficulty;
    private String category;  // Optional: e.g., "Infectious", "Cardiovascular"
}