package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.telegramUser.TelegramUserResponse;
import az.ingress.tgbot.entity.TelegramUser;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface TelegramUserService {
    TelegramUserResponse getById(Long id);
    TelegramUserResponse getByChatId(Long chatId);
    List<TelegramUserResponse> getAll();

    TelegramUser saveOrUpdateTelegramUser(User tgUser);
}