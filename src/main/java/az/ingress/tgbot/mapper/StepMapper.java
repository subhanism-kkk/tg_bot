package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.step.StepCreateRequest;
import az.ingress.tgbot.dto.step.StepResponse;
import az.ingress.tgbot.dto.step.StepUpdateRequest;
import az.ingress.tgbot.entity.Step;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuestionMapper.class})
public interface StepMapper {

    @Mapping(target = "surveyId", source = "survey.id")
    StepResponse toResponse(Step entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "survey", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Step toEntity(StepCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "survey", ignore = true)
    @Mapping(target = "questions", ignore = true)
    void updateEntity(@MappingTarget Step entity, StepUpdateRequest request);
}