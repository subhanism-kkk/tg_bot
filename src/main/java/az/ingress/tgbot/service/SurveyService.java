package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.survey.SurveyCreateRequest;
import az.ingress.tgbot.dto.survey.SurveyResponse;
import az.ingress.tgbot.dto.survey.SurveyUpdateRequest;

import java.util.List;

public interface SurveyService {
    SurveyResponse create(SurveyCreateRequest request);
    SurveyResponse update(Long id, SurveyUpdateRequest request);
    SurveyResponse getById(Long id);
    List<SurveyResponse> getAll();
    void delete(Long id);
    void activate(Long id);
    void deactivate(Long id);
}