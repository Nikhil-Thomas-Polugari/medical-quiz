package Repository;

import com.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.questionId NOT IN " +
           "(SELECT ua.question.questionId FROM UserAnswer ua WHERE ua.user.userId = :userId)")
    List<Question> findUnansweredQuestionsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(q) FROM Question q")
    long getTotalQuestionCount();
}