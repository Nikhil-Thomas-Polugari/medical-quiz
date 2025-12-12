package Service;

import com.quiz.entity.Question;
import com.quiz.entity.User;
import com.quiz.entity.UserAnswer;
import com.quiz.dto.*;
import com.quiz.repository.QuestionRepository;
import com.quiz.repository.UserAnswerRepository;
import com.quiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final Random random = new Random();
    
    public QuestionDTO getRandomUnansweredQuestion(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Question> unansweredQuestions = questionRepository
            .findUnansweredQuestionsByUser(user.getUserId());
        
        if (unansweredQuestions.isEmpty()) {
            return null; // Quiz completed
        }
        
        Question question = unansweredQuestions.get(random.nextInt(unansweredQuestions.size()));
        return convertToDTO(question);
    }
    
    @Transactional
    public AnswerResponseDTO submitAnswer(String username, AnswerSubmissionDTO submission) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Question question = questionRepository.findById(submission.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Check if already answered
        if (userAnswerRepository.existsByUserUserIdAndQuestionQuestionId(
                user.getUserId(), question.getQuestionId())) {
            throw new RuntimeException("Question already answered");
        }
        
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(submission.getAnswer().trim());
        
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUser(user);
        userAnswer.setQuestion(question);
        userAnswer.setUserAnswer(submission.getAnswer());
        userAnswer.setIsCorrect(isCorrect);
        
        userAnswerRepository.save(userAnswer);
        
        String message = isCorrect ? "Correct!" : "Incorrect!";
        return new AnswerResponseDTO(isCorrect, question.getCorrectAnswer(), message);
    }
    
    public ScoreDTO getUserScore(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        long correctAnswers = userAnswerRepository.countCorrectAnswersByUser(user.getUserId());
        long totalAnswered = userAnswerRepository.countTotalAnswersByUser(user.getUserId());
        long totalQuestions = questionRepository.getTotalQuestionCount();
        
        double percentage = totalAnswered > 0 ? 
            (correctAnswers * 100.0) / totalAnswered : 0.0;
        
        boolean quizCompleted = totalAnswered >= totalQuestions;
        
        return new ScoreDTO(correctAnswers, totalAnswered, totalQuestions, 
                           percentage, quizCompleted);
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
