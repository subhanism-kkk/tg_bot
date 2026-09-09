package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.userAnswer.UserAnswerResponse;
import az.ingress.tgbot.dto.userAnswer.UserAnswerSubmitRequest;
import az.ingress.tgbot.entity.UserAnswer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAnswerMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "telegramUserId", source = "telegramUser.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "questionText", source = "question.text")
    @Mapping(target = "selectedOptionIds", expression = "java(mapStringToList(entity.getSelectedOptionIds()))")
    UserAnswerResponse toResponse(UserAnswer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramUser", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "answeredAt", ignore = true)
    @Mapping(target = "selectedOptionIds", expression = "java(mapListToString(request.getSelectedOptionIds()))")
    UserAnswer toEntity(UserAnswerSubmitRequest request);

    default List<Long> mapStringToList(String selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(selectedOptionIds, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    default String mapListToString(List<Long> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(selectedOptionIds);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}