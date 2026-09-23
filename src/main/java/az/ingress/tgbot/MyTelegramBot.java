package az.ingress.tgbot;

import az.ingress.tgbot.service.DynamicSurveyEngine;
import az.ingress.tgbot.service.TelegramUserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final DynamicSurveyEngine surveyEngine;
    private final TelegramUserService telegramUserService;
    private final String botUsername;

    public MyTelegramBot(
            DynamicSurveyEngine surveyEngine,
            TelegramUserService telegramUserService,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken
    ) {
        super(botToken);
        this.surveyEngine = surveyEngine;
        this.telegramUserService = telegramUserService;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        // Extract Telegram API User object and sync with DB entity
        User tgUser = extractUserFromUpdate(update);
        if (tgUser != null) {
            telegramUserService.saveOrUpdateTelegramUser(tgUser);
        }

        // 1. Handle Inline Button Clicks (RADIO, CHECKBOX, and START_SURVEY)
        if (update.hasCallbackQuery()) {

            CallbackQuery cb = update.getCallbackQuery();
            Long chatId = cb.getMessage().getChatId();
            Integer messageId = cb.getMessage().getMessageId();
            String callbackData = cb.getData();

            BotApiMethod<?> response = surveyEngine.handleCallback(chatId, messageId, callbackData);

            if (response != null) {
                execute(response);
            }

            return;
        }

        // 2. Handle User Messages (Text & Shared Contact/Phone)
        if (update.hasMessage()) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            // PHONE: Handle native contact sharing
            if (message.hasContact()) {

                SendMessage removeKeyboardMsg = new SendMessage(String.valueOf(chatId), "📱 Phone number received.");
                removeKeyboardMsg.setReplyMarkup(new ReplyKeyboardRemove(true));
                execute(removeKeyboardMsg);

                String phoneNumber = message.getContact().getPhoneNumber();
                SendMessage response = surveyEngine.handlePhoneNumber(chatId, phoneNumber);

                if (response != null) {
                    execute(response);
                }

                return;
            }

            // TEXT / NUMBER / DATE
            if (message.hasText()) {

                String text = message.getText().trim();

                if ("/start".equalsIgnoreCase(text)) {

                    SendMessage response = surveyEngine.handleStart(chatId);

                    if (response != null) {
                        execute(response);
                    }

                } else {

                    SendMessage response = surveyEngine.handleTextMessage(chatId, text);

                    if (response != null) {
                        execute(response);
                    }
                }
            }
        }
    }

    private User extractUserFromUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        } else if (update.hasMessage()) {
            return update.getMessage().getFrom();
        }
        return null;
    }
}