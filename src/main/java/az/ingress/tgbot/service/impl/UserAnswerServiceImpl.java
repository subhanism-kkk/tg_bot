package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.userAnswer.UserAnswerResponse;
import az.ingress.tgbot.dto.userAnswer.UserAnswerSubmitRequest;
import az.ingress.tgbot.entity.Question;
import az.ingress.tgbot.entity.TelegramUser;
import az.ingress.tgbot.entity.UserAnswer;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.UserAnswerMapper;
import az.ingress.tgbot.repository.QuestionRepository;
import az.ingress.tgbot.repository.TelegramUserRepository;
import az.ingress.tgbot.repository.UserAnswerRepository;
import az.ingress.tgbot.service.UserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAnswerServiceImpl implements UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerMapper userAnswerMapper;

    @Override
    @Transactional
    public UserAnswerResponse submitAnswer(UserAnswerSubmitRequest request) {
        TelegramUser telegramUser = telegramUserRepository.findById(request.getTelegramUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Telegram user not found with id: " + request.getTelegramUserId()));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + request.getQuestionId()));

        UserAnswer userAnswer = userAnswerMapper.toEntity(request);
        userAnswer.setTelegramUser(telegramUser);
        userAnswer.setQuestion(question);

        UserAnswer savedAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toResponse(savedAnswer);
    }

    @Override
    public List<UserAnswerResponse> getResults(Long surveyId, String status, LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime endDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        List<UserAnswer> answers = userAnswerRepository.findFilteredAnswers(
                surveyId, status, startDateTime, endDateTime
        );

        return answers.stream()
                .map(userAnswerMapper::toResponse)
                .toList();
    }
}