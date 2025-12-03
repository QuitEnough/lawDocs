package com.yana.ai.services;

import com.yana.ai.model.Chat;
import com.yana.ai.model.ChatEntry;
import com.yana.ai.model.Role;
import com.yana.ai.repository.ChatRepository;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static com.yana.ai.model.Role.ASSISTANT;
import static com.yana.ai.model.Role.USER;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatService myProxy;

    public List<Chat> getAllChats() {
        return chatRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );
    }

    public Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow();
    }

    public Chat createNewChat(String title) {
        var chat = Chat.builder().title(title).build();
        return chatRepository.save(chat);
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }

    @Transactional
    // передаем в какой чат зашел и что сказал юзер
    // сначала нужно сохранить сообщение в базу, которое только что сказал юзер
    // после того как получим ответ от модели (нужно сходить в модель)
    // нужно сохранить сообщение от модели
    public void proceedInteraction(Long chatId, String prompt) {
        myProxy.addChatEntry(chatId, prompt, USER);
        var answer = chatClient.prompt().user(prompt).call().content();
        myProxy.addChatEntry(chatId, answer, ASSISTANT);
    }

    @Transactional
    public void addChatEntry(Long chatId, String prompt, Role role) {
        var chat = chatRepository.findById(chatId).orElseThrow();
        chat.addChatEntry(ChatEntry.builder()
                .content(prompt)
                .role(role)
                .build());
    }

    public SseEmitter proceedInteractionWithStreaming(Long chatId, String prompt) {

        var sseEmitter = new SseEmitter(0L);
        final var answer = new StringBuilder();

        chatClient.prompt(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .chatResponse()
                .subscribe(response -> processToken(response, sseEmitter, answer),
                        sseEmitter::completeWithError);

        return sseEmitter;
    }

    @SneakyThrows
    private static void processToken(ChatResponse response, SseEmitter sseEmitter, StringBuilder answer) {
        var token = response.getResult().getOutput();
        sseEmitter.send(token);
        answer.append(token.getText());
    }
}
