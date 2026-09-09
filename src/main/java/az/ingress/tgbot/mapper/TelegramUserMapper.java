package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.telegramUser.TelegramUserResponse;
import az.ingress.tgbot.entity.TelegramUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TelegramUserMapper {

    @Mapping(target = "currentStepId", source = "currentStep.id")
    @Mapping(target = "currentQuestionId", source = "currentQuestion.id")
    TelegramUserResponse toResponse(TelegramUser entity);
}