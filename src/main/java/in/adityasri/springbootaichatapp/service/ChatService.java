package in.adityasri.springbootaichatapp.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * — ChatClient (Spring AI 1.0):
 *   ChatClient is the recommended, high-level fluent API for talking to LLMs.
 *   It sits on top of the lower-level ChatModel and reads much closer to how
 *   you'd describe the request in plain English:
 *
 *       chatClient.prompt().user("Hello").call().content();
 *
 *   Compared to using ChatModel directly it gives us:
 *     - a fluent builder (system/user/params in one chain)
 *     - .call()   -> blocking, returns the full response
 *     - .stream() -> reactive, streams tokens as they arrive
 *     - a place to plug in advisors (memory, RAG, logging) later
 *
 * — Dependency Injection:
 *   We inject a ChatClient.Builder (auto-configured by the openai starter) and
 *   build one reusable ChatClient. The builder already carries the model +
 *   options from application.properties.
 */
@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Simple chat — a single user message, no system context.
     * .call().content() blocks until the full reply is ready and returns it as text.
     */
    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * Chat with a system prompt — gives the AI a persona/context.
     * The system message steers the model's behaviour but is not the user's input.
     */
    public String chatWithSystem(String systemContext, String userMessage) {
        return chatClient.prompt()
                .system(systemContext)
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * Streaming chat — emits the response token-by-token as a Flux.
     * The controller relays this as Server-Sent Events so the UI can render
     * the answer as it's being generated (the classic "typing" effect).
     */
    public Flux<String> chatStream(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }
}
