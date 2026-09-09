package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.userAnswer.UserAnswerResponse;
import az.ingress.tgbot.dto.userAnswer.UserAnswerSubmitRequest;

import java.time.LocalDate;
import java.util.List;

public interface UserAnswerService {
    UserAnswerResponse submitAnswer(UserAnswerSubmitRequest request);
    List<UserAnswerResponse> getResults(Long surveyId, String status, LocalDate from, LocalDate to);
}