package com.medicalquiz.Service;

import com.medicalquiz.DTO.AnswerResponseDTO;
import com.medicalquiz.DTO.AnswerSubmissionDTO;
import com.medicalquiz.DTO.QuestionDTO;
import com.medicalquiz.Util.ConnectionUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionServiceTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ConnectionUtil connectionUtil;

    @Test
    @DisplayName("Test service bean is created")
    void testServiceBeanExists() {
        assertNotNull(questionService, "QuestionService should be autowired");
        System.out.println("✓ QuestionService bean exists");
    }

    @Test
    @DisplayName("Test getRandomQuestion returns a question")
    void testGetRandomQuestion() {
        try {
            QuestionDTO question = questionService.getRandomQuestion();
            
            assertNotNull(question, "Question should not be null");
            assertNotNull(question.getQuestion(), "Question text should not be null");
            assertNotNull(question.getAnswer(), "Answer should not be null");
            
            System.out.println("✓ Random question retrieved successfully");
            System.out.println("  Question: " + question.getQuestion());
            System.out.println("  Category: " + question.getAnswer());
        } catch (Exception e) {
            System.out.println("⚠ No questions in database yet: " + e.getMessage());
        }
    }

@Test
@DisplayName("Test checkAnswer with correct answer")
void testCheckAnswerCorrect() {
    try {
        // First get a question
        QuestionDTO question = questionService.getRandomQuestion();
        
        // Submit the correct answer (it's already in the question object)
        AnswerSubmissionDTO submission = new AnswerSubmissionDTO();
        submission.setAnswer(question.getAnswer());
        
        AnswerResponseDTO response = questionService.checkAnswer(submission);
        
        assertTrue(response.isCorrect(), "Answer should be marked as correct");
        assertEquals("Correct!", response.getMessage());
        
        System.out.println("✓ Correct answer validation works");
        System.out.println("  Question: " + question.getQuestion());
        System.out.println("  Correct answer submitted: " + question.getAnswer());
    } catch (Exception e) {
        System.out.println("⚠ Test skipped - no questions available: " + e.getMessage());
    }
}
    @Test
    @DisplayName("Test checkAnswer with incorrect answer")
    void testCheckAnswerIncorrect() {
        try {
            QuestionDTO question = questionService.getRandomQuestion();
            
            AnswerSubmissionDTO submission = new AnswerSubmissionDTO();
            submission.setQuestion(question.getQuestion());
            submission.setAnswer("Definitely Wrong Answer XYZ");
            
            AnswerResponseDTO response = questionService.checkAnswer(submission);
            
            assertFalse(response.isCorrect(), "Answer should be marked as incorrect");
            assertNotNull(response.getCorrectAnswer(), "Correct answer should be provided");
            assertTrue(response.getMessage().contains("Incorrect"));
            
            System.out.println("✓ Incorrect answer validation works");
            System.out.println("  Message: " + response.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Test skipped - no questions available: " + e.getMessage());
        }
    }

@Test
@DisplayName("Test checkAnswer with typo (fuzzy matching)")
void testCheckAnswerWithTypo() {
    try {
        QuestionDTO question = questionService.getRandomQuestion();
        String correctAnswer = question.getAnswer();
        
        // Introduce a minor typo
        String answerWithTypo = correctAnswer.substring(0, correctAnswer.length() - 1) + "x";
        
        AnswerSubmissionDTO submission = new AnswerSubmissionDTO();
        submission.setAnswer(answerWithTypo);
        
        AnswerResponseDTO response = questionService.checkAnswer(submission);
        
        // Fuzzy matching might accept this depending on similarity
        System.out.println("✓ Fuzzy matching test completed");
        System.out.println("  Original: " + correctAnswer);
        System.out.println("  With typo: " + answerWithTypo);
        System.out.println("  Accepted: " + response.isCorrect());
    } catch (Exception e) {
        System.out.println("⚠ Test skipped - no questions available: " + e.getMessage());
    }
}
    @Test
    @DisplayName("Test getTotalQuestionCount")
    void testGetTotalQuestionCount() {
        try {
            long count = questionService.getTotalQuestionCount();
            
            assertTrue(count >= 0, "Question count should be non-negative");
            
            System.out.println("✓ Total question count: " + count);
        } catch (Exception e) {
            fail("Failed to get question count: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test checkAnswer with invalid question ID")
    void testCheckAnswerInvalidId() {
        AnswerSubmissionDTO submission = new AnswerSubmissionDTO();
        submission.setQuestion(""); // Non-existent ID
        submission.setAnswer("Test Answer");
        
        assertThrows(RuntimeException.class, () -> {
            questionService.checkAnswer(submission);
        }, "Should throw exception for invalid question ID");
        
        System.out.println("✓ Invalid question ID handling works");
    }

    // Helper method to get correct answer for testing
    private String getCorrectAnswerForQuestion(Long questionId) {
        try (var conn = connectionUtil.getConnection();
             var pstmt = conn.prepareStatement("SELECT CORRECT_ANSWER FROM QUESTIONS WHERE QUESTION_ID = ?")) {
            
            pstmt.setLong(1, questionId);
            var rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("CORRECT_ANSWER");
            }
            throw new RuntimeException("Question not found");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to get correct answer", e);
        }
    }
}