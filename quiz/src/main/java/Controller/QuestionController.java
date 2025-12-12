package Controller;

import dto;
import service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    
    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuestion(Authentication authentication) {
        try {
            String username = authentication.getName();
            QuestionDTO question = questionService.getRandomUnansweredQuestion(username);
            
            if (question == null) {
                return ResponseEntity.ok().body("Quiz completed! Check your score.");
            }
            
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody AnswerSubmissionDTO submission,
                                         Authentication authentication) {
        try {
            String username = authentication.getName();
            AnswerResponseDTO response = questionService.submitAnswer(username, submission);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/score")
    public ResponseEntity<?> getScore(Authentication authentication) {
        try {
            String username = authentication.getName();
            ScoreDTO score = questionService.getUserScore(username);
            return ResponseEntity.ok(score);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
