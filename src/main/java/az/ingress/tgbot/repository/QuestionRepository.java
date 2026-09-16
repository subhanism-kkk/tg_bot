package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByStepIdOrderByOrderIndexAsc(Long stepId);
    Optional<Question> findFirstByStepIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(Long stepId, Long orderIndex);

    boolean existsByStepIdAndTextIgnoreCase(Long stepId, String trim);

    @Query("SELECT MAX(q.orderIndex) FROM Question q WHERE q.step.id = :stepId")
    Optional<Long> findMaxOrderIndexByStepId(@Param("stepId") Long stepId);

    boolean existsByStepIdAndOrderIndex(Long stepId, Long targetOrderIndex);

    boolean existsByStepIdAndTextIgnoreCaseAndIdNot(Long stepId, String trim, Long id);

    boolean existsByStepIdAndOrderIndexAndIdNot(Long stepId, Long orderIndex, Long id);
}
