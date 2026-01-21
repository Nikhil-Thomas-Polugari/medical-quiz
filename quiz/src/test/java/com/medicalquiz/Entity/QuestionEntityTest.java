package com.medicalquiz.Entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class QuestionEntityTest {

    @Test
    @DisplayName("Test Question entity with all args constructor")
    void testAllArgsConstructor() {
        Question question = new Question(
            1L,
            "What is the diagnosis?",
            "Pneumonia",
            "Medium",
            "Respiratory"
        );

        assertEquals(1L, question.getQuestionId());
        assertEquals("What is the diagnosis?", question.getQuestionText());
        assertEquals("Pneumonia", question.getCorrectAnswer());
        assertEquals("Medium", question.getDifficulty());
        assertEquals("Respiratory", question.getCategory());

        System.out.println("✓ Question all-args constructor works");
    }

    @Test
    @DisplayName("Test Question entity with no args constructor")
    void testNoArgsConstructor() {
        Question question = new Question();

        assertNull(question.getQuestionId());
        assertNull(question.getQuestionText());
        assertNull(question.getCorrectAnswer());
        assertNull(question.getDifficulty());
        assertNull(question.getCategory());

        System.out.println("✓ Question no-args constructor works");
    }

    @Test
    @DisplayName("Test Question entity setters")
    void testSetters() {
        Question question = new Question();

        question.setQuestionId(2L);
        question.setQuestionText("Updated question");
        question.setCorrectAnswer("Updated answer");
        question.setDifficulty("Hard");
        question.setCategory("Cardiovascular");

        assertEquals(2L, question.getQuestionId());
        assertEquals("Updated question", question.getQuestionText());
        assertEquals("Updated answer", question.getCorrectAnswer());
        assertEquals("Hard", question.getDifficulty());
        assertEquals("Cardiovascular", question.getCategory());

        System.out.println("✓ Question setters work");
    }

    @Test
    @DisplayName("Test Question entity getters")
    void testGetters() {
        Question question = new Question(
            3L,
            "Test question",
            "Test answer",
            "Easy",
            "Test category"
        );

        assertEquals(3L, question.getQuestionId());
        assertEquals("Test question", question.getQuestionText());
        assertEquals("Test answer", question.getCorrectAnswer());
        assertEquals("Easy", question.getDifficulty());
        assertEquals("Test category", question.getCategory());

        System.out.println("✓ Question getters work");
    }

    @Test
    @DisplayName("Test Question entity equals (Lombok)")
    void testEquals() {
        Question q1 = new Question(1L, "Q", "A", "M", "C");
        Question q2 = new Question(1L, "Q", "A", "M", "C");
        Question q3 = new Question(2L, "Q", "A", "M", "C");

        assertEquals(q1, q2, "Same questions should be equal");
        assertNotEquals(q1, q3, "Different questions should not be equal");

        System.out.println("✓ Question equals works (Lombok @Data)");
    }

    @Test
    @DisplayName("Test Question entity hashCode (Lombok)")
    void testHashCode() {
        Question q1 = new Question(1L, "Q", "A", "M", "C");
        Question q2 = new Question(1L, "Q", "A", "M", "C");

        assertEquals(q1.hashCode(), q2.hashCode(), "Same questions should have same hashCode");

        System.out.println("✓ Question hashCode works (Lombok @Data)");
    }

    @Test
    @DisplayName("Test Question entity toString (Lombok)")
    void testToString() {
        Question question = new Question(
            1L,
            "Test question",
            "Test answer",
            "Medium",
            "Respiratory"
        );

        String toString = question.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("questionId"));
        assertTrue(toString.contains("questionText"));

        System.out.println("✓ Question toString works (Lombok @Data)");
        System.out.println("  " + toString);
    }
}