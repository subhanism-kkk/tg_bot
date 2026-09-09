package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.questionOption.QuestionOptionCreateRequest;
import az.ingress.tgbot.dto.questionOption.QuestionOptionResponse;
import az.ingress.tgbot.dto.questionOption.QuestionOptionUpdateRequest;

import java.util.List;

public interface QuestionOptionService {
    QuestionOptionResponse create(Long questionId, QuestionOptionCreateRequest request);
    QuestionOptionResponse update(Long id, QuestionOptionUpdateRequest request);
    QuestionOptionResponse getById(Long id);
    List<QuestionOptionResponse> getByQuestionId(Long questionId);
    void delete(Long id);
}