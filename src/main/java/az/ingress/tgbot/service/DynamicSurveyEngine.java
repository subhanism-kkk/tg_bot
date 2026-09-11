package az.ingress.tgbot.service;

import az.ingress.tgbot.entity.Question;
import az.ingress.tgbot.entity.QuestionOption;
import az.ingress.tgbot.entity.Step;
import az.ingress.tgbot.entity.Survey;
import az.ingress.tgbot.entity.TelegramUser;
import az.ingress.tgbot.entity.UserAnswer;
import az.ingress.tgbot.enums.QuestionType;
import az.ingress.tgbot.enums.RegistrationStatus;
import az.ingress.tgbot.repository.QuestionRepository;
import az.ingress.tgbot.repository.StepRepository;
import az.ingress.tgbot.repository.SurveyRepository;
import az.ingress.tgbot.repository.TelegramUserRepository;
import az.ingress.tgbot.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DynamicSurveyEngine {

    private final SurveyRepository surveyRepository;
    private final StepRepository stepRepository;
    private final QuestionRepository questionRepository;
    private final TelegramUserRepository userRepository;
    private final UserAnswerRepository answerRepository;

    /**
     * Handles /start command: creates or resumes user session without advancing state.
     */
    @Transactional
    public SendMessage handleStart(Long chatId) {
        TelegramUser user = userRepository.findByChatId(chatId)
                .orElseGet(() -> userRepository.save(TelegramUser.builder()
                        .chatId(chatId)
                        .registrationStatus(RegistrationStatus.IN_PROGRESS)
                        .build()));

        Optional<Survey> activeSurveyOpt = surveyRepository.findByIsActiveTrue();
        if (activeSurveyOpt.isEmpty()) {
            return message(chatId, "⚠️ There are no active surveys available right now.");
        }

        Survey survey = activeSurveyOpt.get();
        List<Step> steps = stepRepository.findBySurveyIdAndIsActiveTrueOrderByOrderIndexAsc(survey.getId());
        if (steps.isEmpty()) {
            return message(chatId, "⚠️ This survey has no active steps.");
        }

        // 1. Resume existing active question if user is already in progress
        if (user.getRegistrationStatus() == RegistrationStatus.IN_PROGRESS && user.getCurrentQuestion() != null) {
            return renderQuestion(chatId, user.getCurrentQuestion(), user);
        }

        // 2. Start a fresh survey attempt if user has no current active question
        Question firstQuestion = findFirstQuestion(steps);
        if (firstQuestion == null) {
            return message(chatId, "⚠️ This survey has no questions available.");
        }

        user.setCurrentStep(firstQuestion.getStep());
        user.setCurrentQuestion(firstQuestion);
        user.setRegistrationStatus(RegistrationStatus.IN_PROGRESS);
        clearCheckboxSelection(user, null);
        userRepository.save(user);

        return renderQuestion(chatId, firstQuestion, user);
    }

    /**
     * Advances to the next question in sequence after an answer is submitted.
     */
    @Transactional
    public SendMessage processNextQuestion(Long chatId) {
        TelegramUser user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("Telegram user not found: " + chatId));

        Optional<Survey> activeSurveyOpt = surveyRepository.findByIsActiveTrue();
        if (activeSurveyOpt.isEmpty()) {
            return message(chatId, "⚠️ There are no active surveys available right now.");
        }

        Survey survey = activeSurveyOpt.get();
        List<Step> steps = stepRepository.findBySurveyIdAndIsActiveTrueOrderByOrderIndexAsc(survey.getId());

        if (steps.isEmpty()) {
            return message(chatId, "⚠️ This survey has no active steps.");
        }

        Question nextQuestion = findNextQuestion(steps, user);

        if (nextQuestion == null) {
            user.setRegistrationStatus(RegistrationStatus.COMPLETED);
            user.setCurrentStep(null);
            user.setCurrentQuestion(null);
            clearCheckboxSelection(user, null);
            userRepository.save(user);

            SendMessage completionMsg = message(chatId, "🎉 Thank you! You have completed the survey.");
            completionMsg.setReplyMarkup(new ReplyKeyboardRemove(true));
            return completionMsg;
        }

        user.setCurrentStep(nextQuestion.getStep());
        user.setCurrentQuestion(nextQuestion);
        userRepository.save(user);

        return renderQuestion(chatId, nextQuestion, user);
    }

    private Question findFirstQuestion(List<Step> steps) {
        for (Step step : steps) {
            List<Question> questions = questionRepository.findByStepIdOrderByOrderIndexAsc(step.getId());
            if (!questions.isEmpty()) {
                return questions.getFirst();
            }
        }
        return null;
    }

    public Question findNextQuestion(List<Step> steps, TelegramUser user) {
        if (user.getCurrentQuestion() == null) {
            return findFirstQuestion(steps);
        }

        Step currentStep = user.getCurrentStep();
        Question currentQuestion = user.getCurrentQuestion();

        if (currentStep == null) {
            return null;
        }

        Optional<Question> nextQuestionInSameStep = questionRepository
                .findFirstByStepIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(
                        currentStep.getId(), currentQuestion.getOrderIndex());

        if (nextQuestionInSameStep.isPresent()) {
            return nextQuestionInSameStep.get();
        }

        List<Step> nextSteps = stepRepository
                .findBySurveyIdAndIsActiveTrueAndOrderIndexGreaterThanOrderByOrderIndexAsc(
                        currentStep.getSurvey().getId(), currentStep.getOrderIndex());

        for (Step nextStep : nextSteps) {
            List<Question> questions = questionRepository.findByStepIdOrderByOrderIndexAsc(nextStep.getId());
            if (!questions.isEmpty()) {
                return questions.getFirst();
            }
        }

        return null;
    }

    @Transactional
    public SendMessage handleTextMessage(Long chatId, String textInput) {
        String answerText = textInput == null ? "" : textInput.trim();

        if ("/start".equalsIgnoreCase(answerText)) {
            return handleStart(chatId);
        }

        TelegramUser user = userRepository.findByChatId(chatId)
                .orElseGet(() -> userRepository.save(TelegramUser.builder()
                        .chatId(chatId)
                        .registrationStatus(RegistrationStatus.IN_PROGRESS)
                        .build()));

        Question question = user.getCurrentQuestion();

        if (question == null) {
            return message(chatId, "You have no active question. Send /start to begin or restart the survey.");
        }

        if (isMenuOrSystemCommand(answerText)) {
            return message(chatId, "⚠️ Please answer the active question before using navigation buttons.");
        }

        if (question.getType() == QuestionType.RADIO
                || question.getType() == QuestionType.CHECKBOX
                || question.getType() == QuestionType.PHONE) {
            return message(chatId, "⚠️ Please answer the current question using the provided buttons.");
        }

        if (Boolean.TRUE.equals(question.getIsRequired()) && answerText.isBlank()) {
            return message(chatId, "⚠️ This question is required. Please provide an answer.");
        }

        if (answerText.isBlank()) {
            saveTextAnswer(user, question, null);
            return processNextQuestion(chatId);
        }

        String validationError = validateTextAnswer(question, answerText);
        if (validationError != null) {
            return message(chatId, validationError);
        }

        saveTextAnswer(user, question, answerText);
        return processNextQuestion(chatId);
    }

    private boolean isMenuOrSystemCommand(String text) {
        if (text == null || text.isBlank()) return false;
        String lower = text.toLowerCase();
        return lower.contains("menu")
                || lower.contains("help")
                || lower.contains("settings")
                || lower.startsWith("/");
    }

    private String validateTextAnswer(Question question, String answerText) {
        if (question.getValidationRegex() != null && !question.getValidationRegex().isBlank()) {
            try {
                if (!answerText.matches(question.getValidationRegex())) {
                    return "⚠️ Invalid input format. Please try again.";
                }
            } catch (Exception exception) {
                return "⚠️ This question has an invalid validation configuration.";
            }
        }

        if (question.getType() == QuestionType.NUMBER) {
            try {
                Double.parseDouble(answerText);
            } catch (NumberFormatException exception) {
                return "⚠️ Please enter a valid number.";
            }
        }

        if (question.getType() == QuestionType.DATE) {
            try {
                LocalDate.parse(answerText);
            } catch (Exception exception) {
                return "⚠️ Please enter a valid date in YYYY-MM-DD format.";
            }
        }

        return null;
    }

    private void saveTextAnswer(TelegramUser user, Question question, String answerText) {
        UserAnswer answer = answerRepository.findFirstByTelegramUserAndQuestion(user, question)
                .orElseGet(() -> UserAnswer.builder().telegramUser(user).question(question).build());

        answer.setAnswerText(answerText);
        answer.setSelectedOptionIds(null);
        answerRepository.save(answer);
    }

    @Transactional
    public BotApiMethod<?> handleCallback(Long chatId, Integer messageId, String callbackData) {
        TelegramUser user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("Telegram user not found: " + chatId));

        Question currentQuestion = user.getCurrentQuestion();

        if (currentQuestion == null) {
            return handleStart(chatId);
        }

        if (callbackData == null || callbackData.isBlank()) {
            return message(chatId, "⚠️ Invalid action.");
        }

        if (callbackData.startsWith("OPTION:")) {
            return handleOptionCallback(chatId, messageId, user, currentQuestion, callbackData);
        }

        if (callbackData.startsWith("SUBMIT_CHECKBOX:")) {
            return handleCheckboxSubmit(chatId, messageId, user, currentQuestion, callbackData);
        }

        return message(chatId, "⚠️ Unknown action.");
    }

    private BotApiMethod<?> handleOptionCallback(Long chatId, Integer messageId, TelegramUser user, Question currentQuestion, String callbackData) {
        String[] parts = callbackData.split(":");
        if (parts.length != 3) {
            return message(chatId, "⚠️ Invalid option.");
        }

        Long optionId;
        Long questionId;

        try {
            optionId = Long.valueOf(parts[1]);
            questionId = Long.valueOf(parts[2]);
        } catch (NumberFormatException exception) {
            return message(chatId, "⚠️ Invalid option.");
        }

        if (!currentQuestion.getId().equals(questionId)) {
            return message(chatId, "⚠️ This question is no longer active.");
        }

        if (currentQuestion.getType() == QuestionType.RADIO) {
            QuestionOption selectedOption = findQuestionOption(currentQuestion, optionId);
            if (selectedOption == null) {
                return message(chatId, "⚠️ Invalid option.");
            }

            saveRadioAnswer(user, currentQuestion, selectedOption);
            return processNextQuestion(chatId);
        }

        if (currentQuestion.getType() == QuestionType.CHECKBOX) {
            QuestionOption selectedOption = findQuestionOption(currentQuestion, optionId);
            if (selectedOption == null) {
                return message(chatId, "⚠️ Invalid option.");
            }

            toggleCheckboxSelection(user, currentQuestion, optionId);
            Set<Long> selectedIds = getSelectedCheckboxOptions(user, currentQuestion);
            return renderCheckboxQuestionEdit(chatId, messageId, currentQuestion, selectedIds);
        }

        return message(chatId, "⚠️ This question does not support options.");
    }

    private void saveRadioAnswer(TelegramUser user, Question question, QuestionOption option) {
        UserAnswer answer = answerRepository.findFirstByTelegramUserAndQuestion(user, question)
                .orElseGet(() -> UserAnswer.builder().telegramUser(user).question(question).build());

        answer.setAnswerText(option.getText());
        answer.setSelectedOptionIds(String.valueOf(option.getId()));
        answerRepository.save(answer);
    }

    private BotApiMethod<?> handleCheckboxSubmit(Long chatId, Integer messageId, TelegramUser user, Question currentQuestion, String callbackData) {
        String[] parts = callbackData.split(":");
        if (parts.length != 2) {
            return message(chatId, "⚠️ Invalid checkbox action.");
        }

        Long questionId;
        try {
            questionId = Long.valueOf(parts[1]);
        } catch (NumberFormatException exception) {
            return message(chatId, "⚠️ Invalid checkbox action.");
        }

        if (!currentQuestion.getId().equals(questionId)) {
            return message(chatId, "⚠️ This question is no longer active.");
        }

        Set<Long> selectedIds = getSelectedCheckboxOptions(user, currentQuestion);

        if (Boolean.TRUE.equals(currentQuestion.getIsRequired()) && selectedIds.isEmpty()) {
            return renderCheckboxQuestionEdit(chatId, messageId, currentQuestion, selectedIds, "⚠️ Please select at least one option.");
        }

        String selectedOptionIds = selectedIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));

        UserAnswer answer = answerRepository.findFirstByTelegramUserAndQuestion(user, currentQuestion)
                .orElseGet(() -> UserAnswer.builder().telegramUser(user).question(currentQuestion).build());

        answer.setAnswerText(null);
        answer.setSelectedOptionIds(selectedOptionIds);
        answerRepository.save(answer);

        clearCheckboxSelection(user, currentQuestion);

        return processNextQuestion(chatId);
    }

    private Set<Long> getSelectedCheckboxOptions(TelegramUser user, Question question) {
        String draft = user.getDraftCheckboxSelections();
        if (draft == null || draft.isBlank()) {
            return new HashSet<>();
        }

        try {
            return Arrays.stream(draft.split(","))
                    .filter(value -> !value.isBlank())
                    .map(Long::valueOf)
                    .collect(Collectors.toCollection(HashSet::new));
        } catch (NumberFormatException exception) {
            return new HashSet<>();
        }
    }

    private void setSelectedCheckboxOptions(TelegramUser user, Question question, Set<Long> selected) {
        String joined = selected.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
        user.setDraftCheckboxSelections(joined);
        userRepository.save(user);
    }

    private void toggleCheckboxSelection(TelegramUser user, Question question, Long optionId) {
        Set<Long> selected = getSelectedCheckboxOptions(user, question);
        if (selected.contains(optionId)) {
            selected.remove(optionId);
        } else {
            selected.add(optionId);
        }
        setSelectedCheckboxOptions(user, question, selected);
    }

    private void clearCheckboxSelection(TelegramUser user, Question question) {
        user.setDraftCheckboxSelections(null);
        userRepository.save(user);
    }

    private QuestionOption findQuestionOption(Question question, Long optionId) {
        if (question.getOptions() == null) {
            return null;
        }
        return question.getOptions().stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst()
                .orElse(null);
    }

    private SendMessage renderQuestion(Long chatId, Question question, TelegramUser user) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(question.getText());

        if (question.getType() == QuestionType.RADIO) {
            message.setReplyMarkup(buildRadioKeyboard(question));
            return message;
        }

        if (question.getType() == QuestionType.CHECKBOX) {
            Set<Long> selectedIds = (user != null) ? getSelectedCheckboxOptions(user, question) : new HashSet<>();
            message.setReplyMarkup(buildCheckboxKeyboard(question, selectedIds));
            return message;
        }

        if (question.getType() == QuestionType.PHONE) {
            message.setReplyMarkup(buildPhoneKeyboard());
            return message;
        }

        return message;
    }

    private InlineKeyboardMarkup buildRadioKeyboard(Question question) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (question.getOptions() != null) {
            for (QuestionOption option : question.getOptions()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(option.getText());
                button.setCallbackData("OPTION:" + option.getId() + ":" + question.getId());
                rows.add(List.of(button));
            }
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup buildCheckboxKeyboard(Question question, Set<Long> selectedIds) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (question.getOptions() != null) {
            for (QuestionOption option : question.getOptions()) {
                boolean selected = selectedIds.contains(option.getId());
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText((selected ? "☑️ " : "⬜ ") + option.getText());
                button.setCallbackData("OPTION:" + option.getId() + ":" + question.getId());
                rows.add(List.of(button));
            }
        }

        InlineKeyboardButton continueButton = new InlineKeyboardButton();
        continueButton.setText("✅ Continue");
        continueButton.setCallbackData("SUBMIT_CHECKBOX:" + question.getId());
        rows.add(List.of(continueButton));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private EditMessageText renderCheckboxQuestionEdit(Long chatId, Integer messageId, Question question, Set<Long> selectedIds) {
        return renderCheckboxQuestionEdit(chatId, messageId, question, selectedIds, null);
    }

    private EditMessageText renderCheckboxQuestionEdit(Long chatId, Integer messageId, Question question, Set<Long> selectedIds, String errorMessage) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);

        String text = question.getText();
        if (errorMessage != null) {
            text = errorMessage + "\n\n" + question.getText();
        }

        editMessage.setText(text);
        editMessage.setReplyMarkup(buildCheckboxKeyboard(question, selectedIds));
        return editMessage;
    }

    private ReplyKeyboardMarkup buildPhoneKeyboard() {
        KeyboardButton contactButton = new KeyboardButton("📱 Share Phone Number");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        markup.setKeyboard(List.of(row));
        return markup;
    }

    @Transactional
    public SendMessage handlePhoneNumber(Long chatId, String phoneNumber) {
        TelegramUser user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("Telegram user not found: " + chatId));

        Question question = user.getCurrentQuestion();

        if (question == null) {
            return processNextQuestion(chatId);
        }

        if (question.getType() != QuestionType.PHONE) {
            return message(chatId, "⚠️ The current question is not asking for a phone number.");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return message(chatId, "⚠️ Please share a valid phone number.");
        }

        String normalizedPhone = phoneNumber.trim();
        saveTextAnswer(user, question, normalizedPhone);

        return processNextQuestion(chatId);
    }

    private SendMessage message(Long chatId, String text) {
        return new SendMessage(String.valueOf(chatId), text);
    }
}