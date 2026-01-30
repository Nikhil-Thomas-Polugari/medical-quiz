package com.medicalquiz.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicalquiz.DTO.AnswerResponseDTO;
import com.medicalquiz.DTO.AnswerSubmissionDTO;
import com.medicalquiz.DTO.QuestionDTO;
import com.medicalquiz.Service.QuestionService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

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
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody AnswerSubmissionDTO submission) {
        try {
            AnswerResponseDTO response = questionService.checkAnswer(submission);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getTotalQuestions() {
        try {
            long count = questionService.getTotalQuestionCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get question count: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("message", "Question API is running");
        return ResponseEntity.ok(health);
    }
}