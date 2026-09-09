package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.question.QuestionCreateRequest;
import az.ingress.tgbot.dto.question.QuestionResponse;
import az.ingress.tgbot.dto.question.QuestionUpdateRequest;
import az.ingress.tgbot.entity.Question;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuestionOptionMapper.class})
public interface QuestionMapper {

    @Mapping(target = "stepId", source = "step.id")
    QuestionResponse toResponse(Question entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "step", ignore = true)
    @Mapping(target = "options", ignore = true)
    Question toEntity(QuestionCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "step", ignore = true)
    @Mapping(target = "options", ignore = true)
    void updateEntity(@MappingTarget Question entity, QuestionUpdateRequest request);
}