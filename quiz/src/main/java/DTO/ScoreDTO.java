package DTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDTO {
    private long correctAnswers;
    private long totalAnswered;
    private long totalQuestions;
    private double percentage;
    private boolean quizCompleted;
}

