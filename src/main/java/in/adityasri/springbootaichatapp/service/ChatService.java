package in.adityasri.springbootaichatapp.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * — Dependency Injection (DI):
 *   We don't create ChatModel ourselves. Spring injects it because:
 *     - spring-ai-starter-model-openai auto-creates a ChatModel bean
 *     - Spring sees our constructor needs one → hands it in automatically
 *   This is called Constructor Injection (recommended over @Autowired on fields).
 *
 *  — ChatModel (Spring AI):
 *   ChatModel is the core Spring AI interface for interacting with LLMs.
 *   OpenAiChatModel is the concrete class provided by the openai starter.
 *
 *   Key methods:
 *     chatModel.call(String)    -> simple: send text, get text back
 *     chatModel.call(Prompt)    -> full: send messages with roles (system, user, assistant)
 */
@Service
public class ChatService {

    private final ChatModel chatModel;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Simple chat — just a user message, no system context.
     * chatModel.call(String) is a convenience method that wraps your text
     * in a UserMessage internally.
     */
    public String chat(String userMessage) {
        return chatModel.call(userMessage);
    }

    /**
     * Chat with a system prompt — gives the AI a persona/context.
     *
     * LEARNING NOTE — Prompt & Messages:
     *   A "Prompt" in Spring AI = a list of messages with roles:
     *     - SystemMessage  -> sets AI behavior/persona (not shown to end user)
     *     - UserMessage    -> the actual user input
     *     - AssistantMessage -> previous AI responses (for multi-turn)
     *
     *   This mimics the OpenAI Chat Completions API structure:
     *   [ { role: "system", content: "..." }, { role: "user", content: "..." } ]
     */
    public String chatWithSystem(String systemContext, String userMessage) {
        Message systemMessage = new SystemPromptTemplate(systemContext).createMessage();
        Message userMsg = new UserMessage(userMessage);

        Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}