package com.medicalquiz.Service;

import com.medicalquiz.DTO.AnswerResponseDTO;
import com.medicalquiz.DTO.AnswerSubmissionDTO;
import com.medicalquiz.DTO.QuestionDTO;
import com.medicalquiz.Util.ConnectionUtil;
import com.medicalquiz.Exceptions.QuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.sql.*;

@Service
@SessionScope  // Creates one instance per user session
public class QuestionService {
    
    @Autowired
    private ConnectionUtil connectionUtil;
    
    // Store the current question for this session
    private String currentQuestion;
    private String currentAnswer;
    
    public QuestionDTO getRandomQuestion() {
        String query = "SELECT QUESTION, ANSWER FROM MEDICAL_QUIZ.PUBLIC.MEDICAL_QUIZ_QUESTIONS ORDER BY RANDOM() LIMIT 1";
        
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                // Store the question and answer for this session
                currentQuestion = rs.getString("QUESTION");
                currentAnswer = rs.getString("ANSWER");
                
                // Return only the question (NOT the answer - that would be cheating!)
                // We pass null for the answer field since users shouldn't see it
                return new QuestionDTO(currentQuestion, null);
            } else {
                throw new QuestionException("Creator forgot to load questions into the database or the Code has amnesia!");
            }
            
        } catch (SQLException e) {
            throw new QuestionException("Congrats! You made the code blank out so hard, it couldn't grab a question", e);
        }
    }
    
    public AnswerResponseDTO checkAnswer(AnswerSubmissionDTO submission) {
        // Verify we have a current question
        if (currentQuestion == null) {
            throw new QuestionException("No question has been requested yet! Get a question first.");
        }
        
        String userAnswer = submission.getAnswer().trim();
        String correctAnswer = currentAnswer.trim();
        
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
        
        // Update the ANSWERED count if correct
        if (isCorrect) {
            markAsAnswered(currentQuestion);
        }
        
        // Store the answered question and answer before clearing
        String answeredQuestion = currentQuestion;
        String answeredCorrectAnswer = currentAnswer;
        
        // Clear the current question after checking
        currentQuestion = null;
        currentAnswer = null;
        
        return new AnswerResponseDTO(isCorrect, answeredCorrectAnswer, message);
    }
    
    public long getTotalQuestionCount() {
        String query = "SELECT COUNT(*) as total FROM MEDICAL_QUIZ.PUBLIC.MEDICAL_QUIZ_QUESTIONS";
        try (
            Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
        
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0L;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new QuestionException("Exception error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get the current question (for debugging/testing)
     */
    public String getCurrentQuestion() {
        return currentQuestion;
    }
    
    /**
     * Check if there's a current question waiting to be answered
     */
    public boolean hasCurrentQuestion() {
        return currentQuestion != null;
    }
    
    /**
     * Mark the question as answered (set ANSWERED to 1)
     */
    private void markAsAnswered(String question) {
        String query = "UPDATE MEDICAL_QUIZ.PUBLIC.MEDICAL_QUIZ_QUESTIONS SET ANSWERED = 1 WHERE QUESTION = ? AND ANSWERED = 0";
        
        try (Connection conn = connectionUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, question);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            // Log the error but don't fail the answer check
            System.err.println("Failed to mark question as answered: " + e.getMessage());
        }
    }
    
    // Fuzzy matching for typos
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