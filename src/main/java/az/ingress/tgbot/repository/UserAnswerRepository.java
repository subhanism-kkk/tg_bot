package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findFilteredAnswers(Long surveyId, String status, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
