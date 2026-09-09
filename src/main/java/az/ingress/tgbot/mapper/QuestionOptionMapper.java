package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.questionOption.QuestionOptionCreateRequest;
import az.ingress.tgbot.dto.questionOption.QuestionOptionResponse;
import az.ingress.tgbot.dto.questionOption.QuestionOptionUpdateRequest;
import az.ingress.tgbot.entity.QuestionOption;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionOptionMapper {

    @Mapping(target = "questionId", source = "question.id")
    QuestionOptionResponse toResponse(QuestionOption entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    QuestionOption toEntity(QuestionOptionCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    void updateEntity(@MappingTarget QuestionOption entity, QuestionOptionUpdateRequest request);
}