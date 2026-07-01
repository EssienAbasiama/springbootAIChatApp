package in.adityasri.springbootaichatapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * — ChatClient (Spring AI 1.0):
 *   ChatClient is the recommended, high-level fluent API for talking to LLMs.
 *     - .call()   -> blocking, returns the full response
 *     - .stream() -> reactive, streams tokens as they arrive
 *     - advisors  -> cross-cutting behaviour (memory, RAG, logging)
 *
 * — Conversation memory (advisor):
 *   MessageChatMemoryAdvisor is registered as a DEFAULT advisor, so every call
 *   automatically loads prior turns and saves the new ones. Each request passes
 *   a conversationId (ChatMemory.CONVERSATION_ID) so different chats keep
 *   separate histories instead of bleeding into each other.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * Simple chat — a single user message. Memory makes it multi-turn:
     * the advisor replays this conversationId's recent history before the model answers.
     */
    public String chat(String conversationId, String userMessage) {
        log.debug("chat request [conversationId={}], {} chars", conversationId, userMessage.length());
        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    /**
     * Chat with a system prompt — gives the AI a persona/context on top of memory.
     */
    public String chatWithSystem(String conversationId, String systemContext, String userMessage) {
        return chatClient.prompt()
                .system(systemContext)
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    /**
     * Streaming chat — emits the response token-by-token as a Flux, while still
     * reading from and writing to this conversation's memory.
     */
    public Flux<String> chatStream(String conversationId, String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }
}
