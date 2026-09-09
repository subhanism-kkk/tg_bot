package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findBySurveyIdOrderByOrderIndexAsc(Long surveyId);
}
