package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.telegramUser.TelegramUserResponse;

import java.util.List;

public interface TelegramUserService {
    TelegramUserResponse getById(Long id);
    TelegramUserResponse getByChatId(Long chatId);
    List<TelegramUserResponse> getAll();
}