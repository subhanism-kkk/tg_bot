package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    boolean existsByTitle(String title);

    Optional<Survey> findByIsActiveTrue();

    boolean existsByTitleIgnoreCase(String trimmedTitle);
}
