package in.adityasri.springbootaichatapp.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * — Conversation memory:
 *   By default each LLM call is stateless — the model forgets everything the
 *   moment it replies. A ChatMemory stores the running conversation so follow-up
 *   questions ("and what about its population?") have context.
 *
 * — MessageWindowChatMemory:
 *   Keeps a sliding window of the most recent N messages per conversation id,
 *   backed (by default) by an in-memory store. Older messages fall off the
 *   window so the prompt never grows unbounded (and neither does token cost).
 *
 *   Defining this @Bean overrides Spring AI's auto-configured default, letting
 *   us tune the window size. For persistence across restarts you'd swap the
 *   default repository for the JDBC/Cassandra ChatMemoryRepository.
 */
@Configuration
public class ChatConfig {

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }
}
