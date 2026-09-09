package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.survey.SurveyCreateRequest;
import az.ingress.tgbot.dto.survey.SurveyResponse;
import az.ingress.tgbot.dto.survey.SurveyUpdateRequest;
import az.ingress.tgbot.entity.Survey;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {StepMapper.class})
public interface SurveyMapper {

    SurveyResponse toResponse(Survey entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "steps", ignore = true)
    Survey toEntity(SurveyCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "steps", ignore = true)
    void updateEntity(@MappingTarget Survey entity, SurveyUpdateRequest request);
}