package com.medicalquiz.DTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

class DTOTests {

    @Nested
    @DisplayName("QuestionDTO Tests")
    class QuestionDTOTests {

        @Test
        @DisplayName("Test QuestionDTO constructor and getters")
        void testQuestionDTOConstructor() {
            QuestionDTO dto = new QuestionDTO(
                1L,
                "What is the diagnosis?",
                "Medium",
                "Cardiovascular"
            );

            assertEquals(1L, dto.getQuestionId());
            assertEquals("What is the diagnosis?", dto.getQuestionText());
            assertEquals("Medium", dto.getDifficulty());
            assertEquals("Cardiovascular", dto.getCategory());

            System.out.println("✓ QuestionDTO constructor and getters work");
        }

        @Test
        @DisplayName("Test QuestionDTO setters")
        void testQuestionDTOSetters() {
            QuestionDTO dto = new QuestionDTO(1L, "Test", "Easy", "Test");

            dto.setQuestionId(2L);
            dto.setQuestionText("Updated question");
            dto.setDifficulty("Hard");
            dto.setCategory("Neurological");

            assertEquals(2L, dto.getQuestionId());
            assertEquals("Updated question", dto.getQuestionText());
            assertEquals("Hard", dto.getDifficulty());
            assertEquals("Neurological", dto.getCategory());

            System.out.println("✓ QuestionDTO setters work");
        }

        @Test
        @DisplayName("Test QuestionDTO handles null values")
        void testQuestionDTONullValues() {
            QuestionDTO dto = new QuestionDTO(null, null, null, null);

            assertNull(dto.getQuestionId());
            assertNull(dto.getQuestionText());
            assertNull(dto.getDifficulty());
            assertNull(dto.getCategory());

            System.out.println("✓ QuestionDTO handles null values");
        }
    }

    @Nested
    @DisplayName("AnswerSubmissionDTO Tests")
    class AnswerSubmissionDTOTests {

        @Test
        @DisplayName("Test AnswerSubmissionDTO getters and setters")
        void testAnswerSubmissionDTO() {
            AnswerSubmissionDTO dto = new AnswerSubmissionDTO();

            dto.setQuestionId(1L);
            dto.setAnswer("Pneumonia");

            assertEquals(1L, dto.getQuestionId());
            assertEquals("Pneumonia", dto.getAnswer());

            System.out.println("✓ AnswerSubmissionDTO getters and setters work");
        }

        @Test
        @DisplayName("Test AnswerSubmissionDTO default values")
        void testAnswerSubmissionDTODefaults() {
            AnswerSubmissionDTO dto = new AnswerSubmissionDTO();

            assertNull(dto.getQuestionId());
            assertNull(dto.getAnswer());

            System.out.println("✓ AnswerSubmissionDTO default values are null");
        }

        @Test
        @DisplayName("Test AnswerSubmissionDTO with empty answer")
        void testAnswerSubmissionDTOEmptyAnswer() {
            AnswerSubmissionDTO dto = new AnswerSubmissionDTO();
            dto.setQuestionId(1L);
            dto.setAnswer("");

            assertEquals(1L, dto.getQuestionId());
            assertEquals("", dto.getAnswer());

            System.out.println("✓ AnswerSubmissionDTO handles empty strings");
        }
    }

    @Nested
    @DisplayName("AnswerResponseDTO Tests")
    class AnswerResponseDTOTests {

        @Test
        @DisplayName("Test AnswerResponseDTO for correct answer")
        void testAnswerResponseDTOCorrect() {
            AnswerResponseDTO dto = new AnswerResponseDTO(
                true,
                "Pneumonia",
                "Correct!"
            );

            assertTrue(dto.isCorrect());
            assertEquals("Pneumonia", dto.getCorrectAnswer());
            assertEquals("Correct!", dto.getMessage());

            System.out.println("✓ AnswerResponseDTO for correct answers works");
        }

        @Test
        @DisplayName("Test AnswerResponseDTO for incorrect answer")
        void testAnswerResponseDTOIncorrect() {
            AnswerResponseDTO dto = new AnswerResponseDTO(
                false,
                "Pneumonia",
                "Incorrect. The correct answer is: Pneumonia"
            );

            assertFalse(dto.isCorrect());
            assertEquals("Pneumonia", dto.getCorrectAnswer());
            assertTrue(dto.getMessage().contains("Incorrect"));

            System.out.println("✓ AnswerResponseDTO for incorrect answers works");
        }

        @Test
        @DisplayName("Test AnswerResponseDTO setters")
        void testAnswerResponseDTOSetters() {
            AnswerResponseDTO dto = new AnswerResponseDTO(false, "Test", "Test");

            dto.setCorrect(true);
            dto.setCorrectAnswer("Updated Answer");
            dto.setMessage("Updated Message");

            assertTrue(dto.isCorrect());
            assertEquals("Updated Answer", dto.getCorrectAnswer());
            assertEquals("Updated Message", dto.getMessage());

            System.out.println("✓ AnswerResponseDTO setters work");
        }

        @Test
        @DisplayName("Test AnswerResponseDTO with close enough answer")
        void testAnswerResponseDTOCloseEnough() {
            AnswerResponseDTO dto = new AnswerResponseDTO(
                true,
                "Carpal Tunnel Syndrome",
                "Correct! (Close enough - watch your spelling)"
            );

            assertTrue(dto.isCorrect());
            assertTrue(dto.getMessage().contains("Close enough"));

            System.out.println("✓ AnswerResponseDTO for close answers works");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Test full question-answer flow with DTOs")
        void testFullFlow() {
            // Create a question
            QuestionDTO question = new QuestionDTO(
                1L,
                "Patient presents with chest pain. Diagnosis?",
                "Medium",
                "Cardiovascular"
            );

            // Create answer submission
            AnswerSubmissionDTO submission = new AnswerSubmissionDTO();
            submission.setQuestionId(question.getQuestionId());
            submission.setAnswer("Heart Attack");

            // Create response
            AnswerResponseDTO response = new AnswerResponseDTO(
                true,
                "Heart Attack",
                "Correct!"
            );

            // Verify the flow
            assertEquals(question.getQuestionId(), submission.getQuestionId());
            assertTrue(response.isCorrect());
            assertEquals(submission.getAnswer(), response.getCorrectAnswer());

            System.out.println("✓ Full DTO flow works end-to-end");
        }
    }
}