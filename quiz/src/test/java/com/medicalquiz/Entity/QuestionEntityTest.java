package com.medicalquiz.Entity;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class QuestionEntityTest {

    @Test
    @DisplayName("Test Question entity with all args constructor")
    void testAllArgsConstructor() {
        Question question = new Question(
            "A patient presents with fever and cough. What is the diagnosis?",
            "Common Cold",
            0
        );
        
        assertEquals("A patient presents with fever and cough. What is the diagnosis?", question.getQuestion());
        assertEquals("Common Cold", question.getAnswer());
        assertEquals(0, question.getAnswered());

        System.out.println("✓ Question all-args constructor works");
    }

    @Test
    @DisplayName("Test Question entity with no args constructor")
    void testNoArgsConstructor() {
        Question question = new Question("Test question", "Test answer", 5);

        assertNull(question.getQuestion());
        assertNull(question.getAnswer());
        assertNull(question.getAnswered());

        System.out.println("✓ Question no-args constructor works");
    }

    @Test
    @DisplayName("Test Question entity setters")
    void testSetters() {
        Question question = new Question("Test question", "Test answer", 5);

        question.setQuestion("Updated question");
        question.setAnswer("Updated answer");
        question.setAnswered(1);

        assertEquals("Updated question", question.getQuestion());
        assertEquals("Updated answer", question.getAnswer());
        assertEquals(1, question.getAnswered());
        System.out.println("✓ Question setters work");
    }

    @Test
    @DisplayName("Test Question entity getters")
    void testGetters() {
        Question question = new Question(
            "Test question",
            "Test answer",
            10
        );

        assertEquals("Test question", question.getQuestion());
        assertEquals("Test answer", question.getAnswer());
        assertEquals(10, question.getAnswered());

        System.out.println("✓ Question getters work");
    }

    @Test
    @DisplayName("Test Question entity equals (Lombok)")
    void testEquals() {
        Question q1 = new Question("Q", "A", 1);
        Question q2 = new Question("Q", "A", 1);
        Question q3 = new Question("Q", "A", 1);

        assertEquals(q1, q2, "Same questions should be equal");
        assertNotEquals(q1, q3, "Different questions should not be equal");

        System.out.println("✓ Question equals works (Lombok @Data)");
    }

    @Test
    @DisplayName("Test Question entity hashCode (Lombok)")
    void testHashCode() {
        Question q1 = new Question("Q", "A", 1);
        Question q2 = new Question("Q", "A", 1);

        assertEquals(q1.hashCode(), q2.hashCode(), "Same questions should have same hashCode");

        System.out.println("✓ Question hashCode works (Lombok @Data)");
    }

    @Test
    @DisplayName("Test Question entity toString (Lombok)")
    void testToString() {
        Question question = new Question(
            "Test question",
            "Test answer",
            5
        );

        String toString = question.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("question_text") || toString.contains("Test question"));
        assertTrue(toString.contains("answer") || toString.contains("Test answer"));

        System.out.println("✓ Question toString works (Lombok @Data)");
        System.out.println("  " + toString);
    }
}