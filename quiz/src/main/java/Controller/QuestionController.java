package Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DTO.AnswerResponseDTO;
import DTO.AnswerSubmissionDTO;
import DTO.QuestionDTO;
import Service.QuestionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    
    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuestion() {
        try {
            QuestionDTO question = questionService.getRandomQuestion();
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody AnswerSubmissionDTO submission) {
        try {
            AnswerResponseDTO response = questionService.checkAnswer(submission);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getTotalQuestions() {
        try {
            long count = questionService.getTotalQuestionCount();
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}