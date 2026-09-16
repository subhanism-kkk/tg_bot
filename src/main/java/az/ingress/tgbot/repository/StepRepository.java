package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Step;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Long> {

    List<Step> findBySurveyIdOrderByOrderIndexAsc(Long surveyId);

    List<Step> findBySurveyIdAndIsActiveTrueOrderByOrderIndexAsc(Long surveyId);

    List<Step> findBySurveyIdAndIsActiveTrueAndOrderIndexGreaterThanOrderByOrderIndexAsc(
            Long surveyId,
            Long orderIndex
    );

    boolean existsBySurveyIdAndTitleIgnoreCase(Long surveyId, String trim);

    @Query("SELECT MAX(s.orderIndex) FROM Step s WHERE s.survey.id = :surveyId")
    Optional<Long> findMaxOrderIndexBySurveyId(@Param("surveyId") Long surveyId);

    boolean existsBySurveyIdAndOrderIndex(Long surveyId, Long targetOrderIndex);

    boolean existsBySurveyIdAndTitleIgnoreCaseAndIdNot(Long surveyId, String trim, Long id);

    boolean existsBySurveyIdAndOrderIndexAndIdNot(Long surveyId, @NotNull(message = "Order index is required") Long orderIndex, Long id);
}