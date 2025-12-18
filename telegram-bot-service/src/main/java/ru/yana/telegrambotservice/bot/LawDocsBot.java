package ru.yana.telegrambotservice.bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LawDocsBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private TelegramClient telegramClient;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private static final String ADD_DOCUMENT_BUTTON = "\uD83D\uDCE5 Добавить документ";
    private static final String CONSULT_AI_BUTTON = "\uD83E\uDD16 Проконсультироваться с AI";
    private static final String GENERATE_DOCUMENT_BUTTON = "\uD83D\uDCC4 Сгенерировать документ";

    @PostConstruct
    public void init() {
        if (telegramClient == null) {
            this.telegramClient = new OkHttpTelegramClient(getBotToken());
        }
        registerCommands();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendHelpMessage(chatId);
                    break;
                case "/settings":
                    sendSettingsMessage(chatId);
                    break;
                case ADD_DOCUMENT_BUTTON:
                    processAddDocument(chatId);
                    break;
                case CONSULT_AI_BUTTON:
                    processConsultAI(chatId);
                    break;
                case GENERATE_DOCUMENT_BUTTON:
                    processGenerateDocument(chatId);
                    break;
                default:
                    sendDefaultMessage(chatId);
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration() {
        log.info("Bot '{}' successfully registered and running", botUsername);
    }

    public void setTelegramClient(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    private void startCommandReceived(long chatId, String name) {
        var answer = """
            Привет, %s! Добро пожаловать в LawDocs Bot! 🤖
            
            Я помогу вам работать с юридическими документами:
            • 📥 Добавлять и хранить документы
            • 🤖 Консультироваться по документам с помощью AI
            • 📄 Генерировать новые документы
            
            Используйте кнопки ниже или команды:
            /help - помощь
            /settings - настройки
            """.formatted(name);

        sendMessageWithKeyboard(chatId, answer);
    }

    private void sendHelpMessage(long chatId) {
        var helpText = """
            📚 *Помощь по LawDocs Bot*
            
            *Основные команды:*
            /start - начать работу
            /help - это сообщение
            /settings - настройки
            
            *Функционал:*
            📥 *Добавить документ* - загрузите документ для хранения и анализа
            🤖 *Проконсультироваться с AI* - задайте вопросы по вашим документам
            📄 *Сгенерировать документ* - создайте новый документ на основе шаблона
            
            Просто нажмите на нужную кнопку в меню!
            """;

        sendMessage(chatId, helpText);
    }

    private void sendSettingsMessage(long chatId) {
        var settingsText = """
            ⚙️ *Настройки*
            
            Настройки будут доступны в будущих обновлениях.
            Сейчас вы можете:
            • Использовать основные функции бота
            • Загружать документы
            • Консультироваться с AI
            • Генерировать документы
            """;

        sendMessage(chatId, settingsText);
    }

    private void processAddDocument(long chatId) {
        var response = """
            📥 *Добавление документа*
            
            Функция загрузки документов будет реализована в ближайшее время.
            Здесь будет интеграция с File Storage Service (MinIO + вектора).
            
            Вы сможете загружать различные форматы документов для последующего анализа.
            """;

        sendMessage(chatId, response);
    }

    private void processConsultAI(long chatId) {
        var response = """
            🤖 *Консультация с AI*
            
            AI сервис для работы с документами находится в разработке.
            В будущем вы сможете задавать вопросы по вашим документам и получать умные ответы.
            
            Интеграция с AI Service (чат по документам).
            """;

        sendMessage(chatId, response);
    }

    private void processGenerateDocument(long chatId) {
        var response = """
            📄 *Генерация документа*
            
            Генератор документов скоро будет доступен.
            Вы сможете создавать документы на основе шаблонов с помощью AI.
            
            Интеграция с Orchestrator → Template + AI Services.
            """;

        sendMessage(chatId, response);
    }

    private void sendDefaultMessage(long chatId) {
        var response = """
            Я пока не понимаю эту команду. 😕
            Используйте кнопки ниже или команды:
            /help - помощь
            /settings - настройки
            """;

        sendMessageWithKeyboard(chatId, response);
    }

    private void sendMessage(long chatId, String textToSend) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(textToSend)
                .parseMode("Markdown")
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }

    private void sendMessageWithKeyboard(long chatId, String textToSend) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(textToSend)
                .parseMode("Markdown")
                .replyMarkup(createMainKeyboard())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message with keyboard: {}", e.getMessage(), e);
        }
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();

        var row1 = new KeyboardRow();
        row1.add(new KeyboardButton(ADD_DOCUMENT_BUTTON));

        var row2 = new KeyboardRow();
        row2.add(new KeyboardButton(CONSULT_AI_BUTTON));

        var row3 = new KeyboardRow();
        row3.add(new KeyboardButton(GENERATE_DOCUMENT_BUTTON));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }

    private void registerCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("start", "Начать работу с ботом"));
        commands.add(new BotCommand("help", "Получить помощь"));
        commands.add(new BotCommand("settings", "Настройки"));

        try {
            telegramClient.execute(
                    SetMyCommands.builder()
                            .commands(commands)
                            .scope(BotCommandScopeDefault.builder().build())
                            .build()
            );
            log.info("Bot commands registered successfully");
        } catch (TelegramApiException e) {
            log.error("Error setting bot commands: {}", e.getMessage(), e);
        }
    }

}
