package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step, Long> {
}
