package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
