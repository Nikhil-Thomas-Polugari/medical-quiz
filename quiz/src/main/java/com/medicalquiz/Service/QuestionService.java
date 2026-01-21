package com.medicalquiz.Service;

import com.medicalquiz.DTO.AnswerResponseDTO;
import com.medicalquiz.DTO.AnswerSubmissionDTO;
import com.medicalquiz.DTO.QuestionDTO;
import com.medicalquiz.Util.ConnectionUtil;
import com.medicalquiz.Exceptions.QuestionException;
import com.medicalquiz.Exceptions.AnswerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.*;

@Service
public class QuestionService {
    
    @Autowired
    private ConnectionUtil connectionUtil;
    
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
                throw new QuestionException("Creator forgot to load questions into the database or the Code has amnesia!");
            }
            
        } catch (SQLException e) {
            throw new QuestionException("Congrads! You make the code to blank out so hard, it couldn't grab a question", e);
        }
    }
    
    public AnswerResponseDTO checkAnswer(AnswerSubmissionDTO submission) {
    String query = "SELECT CORRECT_ANSWER FROM QUESTIONS WHERE QUESTION_ID = ?";
    
    try (Connection conn = connectionUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setLong(1, submission.getQuestionId());
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String correctAnswer = rs.getString("CORRECT_ANSWER").trim();
                String userAnswer = submission.getAnswer().trim();
                
                // First try exact match (case-insensitive)
                boolean isExactMatch = correctAnswer.equalsIgnoreCase(userAnswer);
                
                // If not exact, try fuzzy match for typos
                boolean isCorrect = isExactMatch || isSimilar(correctAnswer, userAnswer);
                
                String message;
                if (isExactMatch) {
                    message = "Correct!";
                } else if (isCorrect) {
                    message = "Correct! (Close enough - watch your spelling)";
                } else {
                    message = "Incorrect. The correct answer is: " + correctAnswer;
                }
                
                return new AnswerResponseDTO(isCorrect, correctAnswer, message);
            } else {
                throw new QuestionException("Code must have blanked out and lost the question ID!");
            }
        }
        
    } catch (SQLException e) {
        throw new AnswerException("How did you break the answer checking portion?", e);
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
            throw new QuestionException("It seems that the code can't count questions", e);
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