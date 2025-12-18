package ru.yana.telegrambotservice.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppData;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@Slf4j
@Component
public class WebAppHandler {

    @Value("${api.gateway.url:http://localhost:8085}")
    private String apiGatewayUrl;

    private final RestTemplate restTemplate;
    private final TelegramClient telegramClient;

    public WebAppHandler(RestTemplate restTemplate, TelegramClient telegramClient) {
        this.restTemplate = restTemplate;
        this.telegramClient = telegramClient;
    }

    public void handleWebAppData(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasWebAppData()) {
            return;
        }

        WebAppData webAppData = update.getMessage().getWebAppData();
        var initData = webAppData.getData();
        var chatId = update.getMessage().getChatId();

        try {
            var response = authenticateWithTelegram(initData);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> authResponse = response.getBody();
                var accessToken = (String) authResponse.get("accessToken");
                var refreshToken = (String) authResponse.get("refreshToken");

                sendMessage(chatId, buildSuccessMessage(authResponse));
            } else {
                sendMessage(chatId, buildErrorMessage());
            }
        } catch (Exception e) {
            log.error("Failed to authenticate via Telegram", e);
            sendMessage(chatId, buildErrorMessage());
        }
    }

    private ResponseEntity<Map> authenticateWithTelegram(String initData) {
        var url = apiGatewayUrl + "/api/v1/auth/telegram";

        Map<String, String> requestBody = Map.of("initData", initData);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var request = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );
    }

    private String buildSuccessMessage(Map<String, Object> authResponse) {
        return """
            ✅ Аутентификация успешна!
            
            Вы можете использовать:
            • Загрузку документов
            • Консультацию с AI
            • Генерацию документов
            
            Для работы с WebApp перейдите по кнопкам в меню.
            """;
    }

    private String buildErrorMessage() {
        return """
            ❌ Ошибка аутентификации
            
            Пожалуйста, попробуйте снова или обратитесь в поддержку.
            """;
    }

    private void sendMessage(Long chatId, String text) {
        try {
            telegramClient.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(text)
                            .build()
            );
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId: {}", chatId, e);
        }
    }
}
