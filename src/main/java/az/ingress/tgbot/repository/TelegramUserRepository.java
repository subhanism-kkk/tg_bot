package az.ingress.tgbot.repository;

import az.ingress.tgbot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByChatId(Long chatId);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<TelegramUser> findByUsernameIgnoreCase(String username);
}
