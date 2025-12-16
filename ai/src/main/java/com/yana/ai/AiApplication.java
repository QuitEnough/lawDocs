package com.yana.ai;

import com.yana.ai.advisors.expansion.ExpansionQueryAdvisor;
import com.yana.ai.advisors.rag.RagAdvisor;
import com.yana.ai.repository.ChatRepository;
import com.yana.ai.services.PostgresChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiApplication {

    private static final PromptTemplate SYSTEM_PROMPT = new PromptTemplate(
            """
            Ты — Василий "Вася" Пупкин, 42-летний инженер-проектировщик систем вентиляции и кондиционирования. Отвечай от первого лица, кратко, по делу, с легкой иронией и практичным подходом.
            
            Вопрос может быть о СЛЕДСТВИИ факта из Context.
            ВСЕГДА связывай: факт Context → вопрос.
            
            Нет связи, даже косвенной = "Не сталкивался с этим на практике" или "В моей работе такое не пригождалось".
            Есть связь = отвечай на основе своего жизненного и профессионального опыта.
            
            Тон: доброжелательный, немного усталый, скептичный к "модным штучкам", ценит надежность и простые решения.
            """
    );

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatModel chatModel;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
                        ExpansionQueryAdvisor.builder(chatModel)
                                .order(0)
                                .build(),
                        getHistoryAdvisor(1),
                        SimpleLoggerAdvisor.builder()
                                .order(2)
                                .build(),
                        RagAdvisor.builder(vectorStore)
                                .order(3)
                                .build(),
                        SimpleLoggerAdvisor.builder()
                                .order(4)
                                .build()
                )
                .defaultOptions(
                        OllamaOptions.builder()
                                .temperature(0.3)
                                .topP(0.7)
                                .topK(20)
                                .repeatPenalty(1.1)
                                .build()
                )
                .defaultSystem(SYSTEM_PROMPT.render())
                .build();
    }

    private Advisor getHistoryAdvisor(int order) {
        return MessageChatMemoryAdvisor.builder(getChatMemory())
                .order(order)
                .build();
    }

    private ChatMemory getChatMemory() {
        return PostgresChatMemory.builder()
                .maxMessages(8)
                .chatMemoryRepository(chatRepository)
                .build();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AiApplication.class, args);
        ChatClient chatClient = context.getBean(ChatClient.class);
    }

}
