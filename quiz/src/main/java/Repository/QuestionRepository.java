package Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // JpaRepository provides all basic CRUD operations
    // findAll(), findById(), count(), etc.
}