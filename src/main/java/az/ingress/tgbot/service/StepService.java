package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.step.StepCreateRequest;
import az.ingress.tgbot.dto.step.StepResponse;
import az.ingress.tgbot.dto.step.StepUpdateRequest;

import java.util.List;

public interface StepService {
    StepResponse create(Long surveyId, StepCreateRequest request);
    StepResponse update(Long id, StepUpdateRequest request);
    StepResponse getById(Long id);
    List<StepResponse> getBySurveyId(Long surveyId);
    void delete(Long id);
}