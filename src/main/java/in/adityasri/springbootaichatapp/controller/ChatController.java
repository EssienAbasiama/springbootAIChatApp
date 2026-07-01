package in.adityasri.springbootaichatapp.controller;

import in.adityasri.springbootaichatapp.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 *  — @RestController:
 *   Shortcut for @Controller + @ResponseBody. Every method returns data
 *   (JSON/text) directly in the HTTP response body, NOT a view name.
 *
 *  — @RequestMapping("/api/chat"):
 *   All endpoints in this class are prefixed with /api/chat.
 *
 *  — @Validated:
 *   Enables validation of @RequestParam constraints (like the @NotBlank below).
 *   Body validation uses @Valid on the parameter.
 *
 *  — conversationId:
 *   Ties requests to a memory thread. Clients that want multi-turn context send
 *   a stable id (e.g. per user/session); if omitted it falls back to "default".
 */
@RestController
@RequestMapping("/api/chat")
@Validated
public class ChatController {

    private final ChatService chatService;

    // Constructor injection — no @Autowired needed for a single constructor (Spring 4.3+).
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Simple chat endpoint.
     *   GET /api/chat/ask?message=Hello&conversationId=abc
     *
     * @NotBlank rejects null/empty/whitespace-only messages with a 400 before
     * we ever spend a token on the OpenAI call.
     */
    @GetMapping("/ask")
    public ChatResponse ask(
            @RequestParam @NotBlank @Size(max = 4000) String message,
            @RequestParam(defaultValue = "default") String conversationId) {
        return new ChatResponse(chatService.chat(conversationId, message));
    }

    /**
     * Chat with a system prompt — give the AI a custom persona.
     *   POST /api/chat/ask-with-context?conversationId=abc
     *   { "system": "You are a pirate.", "message": "Capital of France?" }
     */
    @PostMapping("/ask-with-context")
    public ChatResponse askWithContext(
            @Valid @RequestBody ChatRequest request,
            @RequestParam(defaultValue = "default") String conversationId) {
        return new ChatResponse(
                chatService.chatWithSystem(conversationId, request.system(), request.message()));
    }

    /**
     * Streaming chat endpoint — Server-Sent Events (text/event-stream).
     * Returns a Flux; Spring MVC streams each chunk to the client as it arrives.
     *   GET /api/chat/stream?message=Tell me a story&conversationId=abc
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
            @RequestParam @NotBlank @Size(max = 4000) String message,
            @RequestParam(defaultValue = "default") String conversationId) {
        return chatService.chatStream(conversationId, message);
    }

    /**
     * Clears a conversation's memory so the next message starts a fresh thread.
     *   DELETE /api/chat/conversation/abc
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ChatResponse clearConversation(@PathVariable String conversationId) {
        chatService.clearConversation(conversationId);
        return new ChatResponse("Conversation '" + conversationId + "' cleared.");
    }

    /**
     * Request DTO — @NotBlank guards both fields; @Size caps abuse.
     */
    record ChatRequest(
            @NotBlank @Size(max = 4000) String system,
            @NotBlank @Size(max = 4000) String message) {}

    /** Response DTO — wrapping the answer in JSON keeps the API extensible. */
    record ChatResponse(String answer) {}
}
