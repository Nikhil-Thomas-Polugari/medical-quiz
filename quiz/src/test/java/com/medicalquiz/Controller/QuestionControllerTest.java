package com.medicalquiz.Controller;

import com.medicalquiz.DTO.AnswerResponseDTO;
import com.medicalquiz.DTO.AnswerSubmissionDTO;
import com.medicalquiz.DTO.QuestionDTO;
import com.medicalquiz.Service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService questionService;

    private QuestionDTO sampleQuestion;
    private AnswerSubmissionDTO sampleSubmission;
    private AnswerResponseDTO correctResponse;

    @BeforeEach
    void setUp() {
        sampleQuestion = new QuestionDTO(
            "A patient presents with fever and cough. What is the diagnosis?", "Pneumonia");

        sampleSubmission = new AnswerSubmissionDTO();
        sampleSubmission.setAnswer("Pneumonia");

        correctResponse = new AnswerResponseDTO(true, "Pneumonia", "Correct!");
    }

    @Test
    @DisplayName("GET /api/questions/random - Should return random question")
    void testGetRandomQuestion() throws Exception {
        when(questionService.getRandomQuestion()).thenReturn(sampleQuestion);

        mockMvc.perform(get("/api/questions/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value(sampleQuestion.getQuestion()))
                .andExpect(jsonPath("$.answered").value(sampleQuestion.getAnswer()));
        System.out.println("✓ GET /api/questions/random returns question successfully");
    }

    @Test
    @DisplayName("GET /api/questions/random - Should handle errors")
    void testGetRandomQuestionError() throws Exception {
        when(questionService.getRandomQuestion())
                .thenThrow(new RuntimeException("No questions available"));

        mockMvc.perform(get("/api/questions/random"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No questions available"));

        System.out.println("✓ GET /api/questions/random handles errors properly");
    }

    @Test
    @DisplayName("POST /api/questions/answer - Should check correct answer")
    void testSubmitCorrectAnswer() throws Exception {
        when(questionService.checkAnswer(any(AnswerSubmissionDTO.class)))
                .thenReturn(correctResponse);

        mockMvc.perform(post("/api/questions/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleSubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.correctAnswer").value("Pneumonia"))
                .andExpect(jsonPath("$.message").value("Correct!"));

        System.out.println("✓ POST /api/questions/answer validates correct answers");
    }

    @Test
    @DisplayName("POST /api/questions/answer - Should check incorrect answer")
    void testSubmitIncorrectAnswer() throws Exception {
        AnswerResponseDTO incorrectResponse = new AnswerResponseDTO(
            false,
            "Pneumonia",
            "Incorrect. The correct answer is: Pneumonia"
        );

        when(questionService.checkAnswer(any(AnswerSubmissionDTO.class)))
                .thenReturn(incorrectResponse);

        mockMvc.perform(post("/api/questions/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleSubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false))
                .andExpect(jsonPath("$.correctAnswer").value("Pneumonia"));

        System.out.println("✓ POST /api/questions/answer handles incorrect answers");
    }

    @Test
    @DisplayName("POST /api/questions/answer - Should handle errors")
    void testSubmitAnswerError() throws Exception {
        when(questionService.checkAnswer(any(AnswerSubmissionDTO.class)))
                .thenThrow(new RuntimeException("Question not found"));

        mockMvc.perform(post("/api/questions/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleSubmission)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Question not found"));

        System.out.println("✓ POST /api/questions/answer handles errors properly");
    }

    @Test
    @DisplayName("GET /api/questions/count - Should return question count")
    void testGetTotalQuestions() throws Exception {
        when(questionService.getTotalQuestionCount()).thenReturn(500L);

        mockMvc.perform(get("/api/questions/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("500"));

        System.out.println("✓ GET /api/questions/count returns total count");
    }

    @Test
    @DisplayName("GET /api/questions/count - Should handle errors")
    void testGetTotalQuestionsError() throws Exception {
        when(questionService.getTotalQuestionCount())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/questions/count"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Database error"));

        System.out.println("✓ GET /api/questions/count handles errors properly");
    }

    @Test
    @DisplayName("Test CORS headers are present")
    void testCorsHeaders() throws Exception {
        when(questionService.getRandomQuestion()).thenReturn(sampleQuestion);

        mockMvc.perform(get("/api/questions/random")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));

        System.out.println("✓ CORS headers are properly configured");
    }
}