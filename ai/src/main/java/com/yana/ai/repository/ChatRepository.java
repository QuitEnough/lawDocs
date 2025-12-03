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
public interface ChatRepository extends JpaRepository<Chat, Long> {

}
