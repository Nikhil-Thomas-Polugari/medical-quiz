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
                "A patient presents with fever and cough. What is the diagnosis?",
                "Pneumonia"
            );

            assertEquals("A patient presents with fever and cough. What is the diagnosis?", dto.getQuestion());
            assertEquals("Pneumonia", dto.getAnswer());

            System.out.println("✓ QuestionDTO constructor and getters work");
        }

        @Test
        @DisplayName("Test QuestionDTO setters")
        void testQuestionDTOSetters() {
            QuestionDTO dto = new QuestionDTO(
                "A patient presents with White patches in mouth, Skin rash and itching and Vaginal discharge. What is the most likely diagnosis?",
                "Candidiasis"
            );

            dto.setQuestion("Updated question");
            dto.setAnswer("Updated answer");

            assertEquals("Updated question", dto.getQuestion());
            assertEquals("Updated answer", dto.getAnswer());

            System.out.println("✓ QuestionDTO setters work");
        }

        @Test
        @DisplayName("Test QuestionDTO handles null values")
        void testQuestionDTONullValues() {
            QuestionDTO dto = new QuestionDTO(null, null);

            assertNull(dto.getQuestion());
            assertNull(dto.getAnswer());

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

            dto.setAnswer("Pneumonia");

            assertEquals("Pneumonia", dto.getAnswer());

            System.out.println("✓ AnswerSubmissionDTO getters and setters work");
        }

        @Test
        @DisplayName("Test AnswerSubmissionDTO default values")
        void testAnswerSubmissionDTODefaults() {
            AnswerSubmissionDTO dto = new AnswerSubmissionDTO();

            assertNull(dto.getAnswer());

            System.out.println("✓ AnswerSubmissionDTO default values are null");
        }

        @Test
        @DisplayName("Test AnswerSubmissionDTO with empty answer")
        void testAnswerSubmissionDTOEmptyAnswer() {
            AnswerSubmissionDTO dto = new AnswerSubmissionDTO();
            dto.setAnswer("");

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
                "A patient presents with fever and cough. What is the diagnosis?",
                "Pneumonia"
            );

            // Create answer submission
            AnswerSubmissionDTO submission = new AnswerSubmissionDTO();
            submission.setAnswer("Pneumonia");

            // Create response
            AnswerResponseDTO response = new AnswerResponseDTO(
                true,
                "Pneumonia",
                "Correct!"
            );

            // Verify the flow
            assertNotNull(question.getQuestion());
            assertEquals("Pneumonia", submission.getAnswer());
            assertTrue(response.isCorrect());
            assertEquals(question.getAnswer(), response.getCorrectAnswer());

            System.out.println("✓ Full DTO flow works end-to-end");
        }
    }
}