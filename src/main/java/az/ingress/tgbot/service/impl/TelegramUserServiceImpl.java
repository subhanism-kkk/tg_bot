package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.telegramUser.TelegramUserResponse;
import az.ingress.tgbot.entity.TelegramUser;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.TelegramUserMapper;
import az.ingress.tgbot.repository.TelegramUserRepository;
import az.ingress.tgbot.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TelegramUserServiceImpl implements TelegramUserService {
    private final TelegramUserRepository repository;
    private final TelegramUserMapper mapper;

    @Override
    public TelegramUserResponse getById(Long id) {
        TelegramUser user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Telegram User not found with id: " + id));
        return mapper.toResponse(user);
    }

    @Override
    public TelegramUserResponse getByChatId(Long chatId) {
        TelegramUser user = repository.findByChatId(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Telegram User not found with chatId: " + chatId));
        return mapper.toResponse(user);
    }

    @Override
    public List<TelegramUserResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();    }
}
