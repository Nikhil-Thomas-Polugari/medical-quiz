package com.medicalquiz.Service;

import com.medicalquiz.DTO.AnswerResponseDTO;
import com.medicalquiz.DTO.AnswerSubmissionDTO;
import com.medicalquiz.DTO.QuestionDTO;
import com.medicalquiz.Exceptions.QuestionException;
import com.medicalquiz.Exceptions.AnswerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class QuestionService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;  // ⭐ Use this instead of ConnectionUtil
    
    private String currentQuestion;
    private String currentAnswer;
    
    public QuestionDTO getRandomQuestion() {
        String query = "SELECT QUESTION, ANSWER, ANSWERED FROM QUESTIONS ORDER BY RANDOM() LIMIT 1";
        
        try {
            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
                currentQuestion = rs.getString("QUESTION");
                currentAnswer = rs.getString("ANSWER");
                
                return new QuestionDTO(
                    currentQuestion,
                    rs.getString("ANSWER")
                );
            });
        } catch (Exception e) {
            throw new QuestionException("Failed to get random question: " + e.getMessage(), e);
        }
    }
    
    public AnswerResponseDTO checkAnswer(AnswerSubmissionDTO submission) {
        if (currentQuestion == null) {
            throw new QuestionException("No question has been requested yet! Get a question first.");
        }
        
        if (!currentQuestion.equals(submission.getQuestion())) {
            throw new QuestionException("This is not the current question! Please answer the question that was asked.");
        }
        
        String userAnswer = submission.getAnswer().trim();
        String correctAnswer = currentAnswer.trim();
        
        boolean isExactMatch = correctAnswer.equalsIgnoreCase(userAnswer);
        boolean isCorrect = isExactMatch || isSimilar(correctAnswer, userAnswer);
        
        String message;
        if (isExactMatch) {
            message = "Correct!";
        } else if (isCorrect) {
            message = "Correct! (Close enough - watch your spelling)";
        } else {
            message = "Incorrect. The correct answer is: " + correctAnswer;
        }
        
        if (isCorrect) {
            updateAnsweredCount(currentQuestion);
        }
        
        currentQuestion = null;
        currentAnswer = null;
        
        return new AnswerResponseDTO(isCorrect, correctAnswer, message);
    }
    
    public long getTotalQuestionCount() {
        String query = "SELECT COUNT(*) FROM QUESTIONS";
        
        try {
            Long count = jdbcTemplate.queryForObject(query, Long.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new QuestionException("Failed to count questions: " + e.getMessage(), e);
        }
    }
    
    public String getCurrentQuestion() {
        return currentQuestion;
    }
    
    public boolean hasCurrentQuestion() {
        return currentQuestion != null;
    }
    
    private void updateAnsweredCount(String question) {
        String query = "UPDATE QUESTIONS SET ANSWERED = ANSWERED + 1 WHERE QUESTION = ?";
        
        try {
            jdbcTemplate.update(query, question);
        } catch (Exception e) {
            System.err.println("Failed to update answered count: " + e.getMessage());
        }
    }
    
    private boolean isSimilar(String correct, String user) {
        correct = correct.replaceAll("\\s+", "").toLowerCase();
        user = user.replaceAll("\\s+", "").toLowerCase();
        
        if (correct.equals(user)) return true;
        if (user.contains(correct) || correct.contains(user)) return true;
        
        int distance = levenshteinDistance(correct, user);
        int maxLength = Math.max(correct.length(), user.length());
        double similarity = 1.0 - ((double) distance / maxLength);
        
        return similarity >= 0.8;
    }
    
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[a.length()][b.length()];
    }
}