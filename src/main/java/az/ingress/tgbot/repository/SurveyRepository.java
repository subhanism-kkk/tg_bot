package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    List<Survey> findByIsActiveTrueOrderByOrderIndexAsc();

    Optional<Survey> findFirstByIsActiveTrueAndOrderIndexGreaterThanOrderByOrderIndexAsc(
            Long orderIndex
    );

    Optional<Survey> findFirstByOrderByOrderIndexDesc();

    boolean existsByOrderIndex(Long orderIndex);

    boolean existsByOrderIndexAndIdNot(Long orderIndex, Long id);
}