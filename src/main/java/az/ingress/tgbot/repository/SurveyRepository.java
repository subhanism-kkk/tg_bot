package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
