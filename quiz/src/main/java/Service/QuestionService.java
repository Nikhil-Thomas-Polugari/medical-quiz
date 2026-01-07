package Service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DTO.AnswerResponseDTO;
import DTO.AnswerSubmissionDTO;
import DTO.QuestionDTO;
import Entity.Question;
import Repository.QuestionRepository;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    
    private final Random random = new Random();
    
    public QuestionDTO getRandomQuestion() {
        List<Question> allQuestions = questionRepository.findAll();
        
        if (allQuestions.isEmpty()) {
            throw new RuntimeException("No questions available");
        }
        
        Question question = allQuestions.get(random.nextInt(allQuestions.size()));
        return convertToDTO(question);
    }
    
    public AnswerResponseDTO checkAnswer(AnswerSubmissionDTO submission) {
        Question question = questionRepository.findById(submission.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        boolean isCorrect = question.getCorrectAnswer()
            .equalsIgnoreCase(submission.getAnswer().trim());
        
        String message = isCorrect ? "Correct!" : "Incorrect!";
        return new AnswerResponseDTO(isCorrect, question.getCorrectAnswer(), message);
    }
    
    public long getTotalQuestionCount() {
        return questionRepository.count();
    }
    
    private QuestionDTO convertToDTO(Question question) {
        return new QuestionDTO(
            question.getQuestionId(),
            question.getQuestionText(),
            question.getOptionA(),
            question.getOptionB(),
            question.getOptionC(),
            question.getOptionD(),
            question.getDifficulty()
        );
    }
}