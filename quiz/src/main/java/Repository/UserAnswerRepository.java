package Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Entity.UserAnswer;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.user.userId = :userId AND ua.isCorrect = true")
    long countCorrectAnswersByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.user.userId = :userId")
    long countTotalAnswersByUser(@Param("userId") Long userId);
    
    boolean existsByUserUserIdAndQuestionQuestionId(Long userId, Long questionId);
}

