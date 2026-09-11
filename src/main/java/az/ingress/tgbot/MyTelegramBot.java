package az.ingress.tgbot;

import az.ingress.tgbot.service.DynamicSurveyEngine;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final DynamicSurveyEngine surveyEngine;
    private final String botUsername;

    public MyTelegramBot(
            DynamicSurveyEngine surveyEngine,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken) {
        super(botToken);
        this.surveyEngine = surveyEngine;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        // 1. Handle Inline Button Clicks (RADIO & CHECKBOX)
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

            // TEXT / NUMBER / DATE: Handle normal text updates
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
}