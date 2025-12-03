package com.yana.ai.repository;

import com.yana.ai.model.Chat;
import com.yana.ai.model.ChatEntry;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>, ChatMemoryRepository {

    @Override
    default List<String> findConversationIds() {
        return findAll().stream()
                .map(Chat::getId)
                .map(String::valueOf)
                .toList();
    }

    @Override
    default List<Message> findByConversationId(String conversationId) {
//        if (conversationId == ChatMemory.DEFAULT_CONVERSATION_ID) return new ArrayList<>();
        var chat = findById(Long.valueOf(conversationId)).orElseThrow();
        return chat.getHistory().stream()
                .map(ChatEntry::toMessage)
                .toList();
    }

    @Override
    default void saveAll(String conversationId, List<Message> messages) {
        var chat = findById(Long.valueOf(conversationId)).orElseThrow();
        messages.stream()
                .map(ChatEntry::toChatEntry)
                .forEach(chat::addChatEntry);
        save(chat);
    }

    @Override
    default void deleteByConversationId(String conversationId) {
        // not implemented
    }

}
