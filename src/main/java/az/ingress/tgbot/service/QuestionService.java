package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.question.QuestionCreateRequest;
import az.ingress.tgbot.dto.question.QuestionResponse;
import az.ingress.tgbot.dto.question.QuestionUpdateRequest;

import java.util.List;

public interface QuestionService {
    QuestionResponse create(Long stepId, QuestionCreateRequest request);
    QuestionResponse update(Long id, QuestionUpdateRequest request);
    QuestionResponse getById(Long id);
    List<QuestionResponse> getByStepId(Long stepId);
    void delete(Long id);
}