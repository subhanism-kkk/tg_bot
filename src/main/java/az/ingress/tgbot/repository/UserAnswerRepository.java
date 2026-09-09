package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    @Query("""
        SELECT ua FROM UserAnswer ua
        JOIN FETCH ua.question q
        JOIN FETCH q.step s
        JOIN FETCH s.survey sur
        JOIN FETCH ua.telegramUser tu
        WHERE (:surveyId IS NULL OR sur.id = :surveyId)
          AND (:status IS NULL OR sur.isActive = CASE WHEN :status = 'ACTIVE' THEN true ELSE false END)
          AND (:startDateTime IS NULL OR ua.answeredAt >= :startDateTime)
          AND (:endDateTime IS NULL OR ua.answeredAt <= :endDateTime)
        ORDER BY ua.answeredAt DESC
    """)
    List<UserAnswer> findFilteredAnswers(
            @Param("surveyId") Long surveyId,
            @Param("status") String status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}