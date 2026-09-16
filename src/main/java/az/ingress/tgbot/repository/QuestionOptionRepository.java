package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionId(Long questionId);

    boolean existsByQuestionIdAndTextIgnoreCase(Long questionId, String text);

    boolean existsByQuestionIdAndOrderIndex(Long questionId, Long orderIndex);

    boolean existsByQuestionIdAndTextIgnoreCaseAndIdNot(Long questionId, String trim, Long id);

    boolean existsByQuestionIdAndOrderIndexAndIdNot(Long questionId, Long orderIndex, Long id);

    @Query("SELECT MAX(o.orderIndex) FROM QuestionOption o WHERE o.question.id = :questionId")
    Optional<Long> findMaxOrderIndexByQuestionId(@Param("questionId") Long questionId);
}
