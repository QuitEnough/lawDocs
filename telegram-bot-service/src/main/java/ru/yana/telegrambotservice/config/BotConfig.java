package ru.yana.telegrambotservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;

@Slf4j
@Configuration
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.http.proxy.enabled:false}")
    private boolean proxyEnabled;

    @Value("${telegram.http.proxy.host:}")
    private String proxyHost;

    @Value("${telegram.http.proxy.port:50505}")
    private int proxyPort;

    @Value("${telegram.http.timeout.seconds:30}")
    private int timeoutSeconds;

    /**
     * Создаем кастомный OkHttpClient для контроля таймаутов и прокси
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .readTimeout(Duration.ofSeconds(timeoutSeconds))
                .writeTimeout(Duration.ofSeconds(timeoutSeconds));

        if (proxyEnabled && !proxyHost.isEmpty()) {
            log.info("Configuring proxy for Telegram client: {}:{}", proxyHost, proxyPort);
            // builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
            // Если нужна аутентификация: .proxyAuthenticator(...)
        }

        return builder.build();
    }

    /**
     * TelegramClient с кастомным OkHttpClient
     */
    @Bean
    public TelegramClient telegramClient(OkHttpClient okHttpClient) {
        return new OkHttpTelegramClient(okHttpClient, botToken);
    }

    /**
     * TelegramBotsApplication с кастомным ObjectMapper и OkHttpClient
     */
    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            OkHttpClient okHttpClient) {
        return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> okHttpClient);
    }

}
