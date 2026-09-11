package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByStepIdOrderByOrderIndexAsc(Long stepId);
    Optional<Question> findFirstByStepIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(Long stepId, Long orderIndex);
}
