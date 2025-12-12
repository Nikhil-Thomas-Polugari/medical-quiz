package DTO;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class AnswerSubmissionDTO {
    @NotNull(message = "Question ID is required")
    private Long questionId;
    
    @NotBlank(message = "Answer is required")
    private String answer;
}

