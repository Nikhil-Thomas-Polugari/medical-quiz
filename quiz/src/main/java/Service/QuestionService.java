package com.medicalquiz.service;

import com.medicalquiz.entity.Question;
import com.medicalquiz.dto.*;
import com.medicalquiz.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.Random;

@Service
public class QuestionService {
    
    @Autowired
    private ConnectionUtil connectionUtil;
    
    private final Random random = new Random();
    
    public QuestionDTO getRandomQuestion() {
        String query = "SELECT QUESTION_ID, QUESTION_TEXT, DIFFICULTY, CATEGORY FROM QUESTIONS ORDER BY RANDOM() LIMIT 1";
        
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return new QuestionDTO(
                    rs.getLong("QUESTION_ID"),
                    rs.getString("QUESTION_TEXT"),
                    rs.getString("DIFFICULTY"),
                    rs.getString("CATEGORY")
                );
            } else {
                throw new RuntimeException("No questions available");
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching random question", e);
        }
    }
    
    public AnswerResponseDTO checkAnswer(AnswerSubmissionDTO submission) {
        String query = "SELECT CORRECT_ANSWER FROM QUESTIONS WHERE QUESTION_ID = ?";
        
        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, submission.getQuestionId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String correctAnswer = rs.getString("CORRECT_ANSWER").trim().toLowerCase();
                    String userAnswer = submission.getAnswer().trim().toLowerCase();
                    
                    // Exact match
                    boolean isCorrect = correctAnswer.equals(userAnswer);
                    
                    // Optional: Fuzzy matching (allow minor typos)
                    // isCorrect = isSimilar(correctAnswer, userAnswer);
                    
                    String message = isCorrect ? "Correct!" : "Incorrect!";
                    return new AnswerResponseDTO(isCorrect, rs.getString("CORRECT_ANSWER"), message);
                } else {
                    throw new RuntimeException("Question not found");
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking answer", e);
        }
    }
    
    public long getTotalQuestionCount() {
        String query = "SELECT COUNT(*) as total FROM QUESTIONS";
        
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error counting questions", e);
        }
    }
    
    // Optional: Fuzzy matching for typos
    private boolean isSimilar(String correct, String user) {
        // Remove spaces and convert to lowercase
        correct = correct.replaceAll("\\s+", "").toLowerCase();
        user = user.replaceAll("\\s+", "").toLowerCase();
        
        // Exact match after cleanup
        if (correct.equals(user)) return true;
        
        // Check if user answer contains the correct answer
        if (user.contains(correct) || correct.contains(user)) return true;
        
        // Calculate similarity (Levenshtein distance)
        int distance = levenshteinDistance(correct, user);
        int maxLength = Math.max(correct.length(), user.length());
        double similarity = 1.0 - ((double) distance / maxLength);
        
        // Accept if 80% similar (allows 1-2 letter typos)
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
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1),     // insertion
                    dp[i - 1][j - 1] + cost // substitution
                );
            }
        }
        
        return dp[a.length()][b.length()];
    }
}