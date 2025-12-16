package com.yana.ai.advisors.expansion;

import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;

@Builder
public class ExpansionQueryAdvisor implements BaseAdvisor {

    private static final PromptTemplate template = PromptTemplate.builder()
            .template("""
        Instruction: Расширь поисковый запрос, добавив наиболее релевантные термины из жизни и профессии Васи Пупкина.
        
        СПЕЦИАЛИЗАЦИЯ ВАСИ ПУПКИНА:
        - Профессия: инженер-проектировщик, теплоэнергетик, вентиляция и кондиционирование, системы ОВК (отопление, вентиляция, кондиционирование)
        - Характер: надежность, перфекционизм, интроверт, практичность, скептицизм, "золотые руки"
        - Семья и быт: жена Катя, дети Миша и Аня, гараж, рыбалка, дача, хрущевка, ипотека
        - Этапы жизни: детство в провинции, политехнический институт, карьера в проектной организации, стабильность
        
        ПРАВИЛА:
        1. Сохрани ВСЕ слова из исходного вопроса
        2. Добавь МАКСИМУМ ПЯТЬ наиболее важных термина
        3. Выбирай самые специфичные и релевантные слова из контекста Васи
        4. Результат - простой список слов через пробел

        СТРАТЕГИЯ ВЫБОРА:
        - Приоритет: профессиональные термины и специфичные детали из биографии
        - Избегай общих абстрактных слов
        - Фокусируйся на конкретных аспектах жизни и работы Васи

        ПРИМЕРЫ:
        "работа инженера" → "работа инженера проектирование вентиляция теплоэнергетика"
        "отдых на природе" → "отдых на природе рыбалка гараж дача"
        "семейные ценности" → "семейные ценности жена дети ипотека стабильность"
        "совет по карьере" → "совет по карьере проектная организация старший специалист надежность"

        Question: {question}
        Expanded query:
        """).build();

    public static final String ENRICHED_QUESTION = "ENRICHED_QUESTION";
    public static final String ORIGINAL_QUESTION = "ORIGINAL_QUESTION";
    public static final String EXPANSION_RATIO = "EXPANSION_RATIO";

    private ChatClient chatClient;

    @Getter
    private final int order;

    public static ExpansionQueryAdvisorBuilder builder(ChatModel chatModel) {
        return new ExpansionQueryAdvisorBuilder()
                .chatClient(ChatClient.builder(chatModel)
                        .defaultOptions(OllamaOptions.builder()
                                .temperature(0.0)
                                .topK(1)
                                .topP(0.1)
                                .repeatPenalty(1.0)
                                .build())
                        .build());
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String userQuestion = chatClientRequest.prompt().getUserMessage().getText();
        String enrichedQuestion = chatClient.prompt().user("extender query").call().content();

        return chatClientRequest.mutate()
                .context(ENRICHED_QUESTION, enrichedQuestion)
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

}
