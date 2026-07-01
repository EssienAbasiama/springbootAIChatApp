package in.adityasri.springbootaichatapp.controller;

import in.adityasri.springbootaichatapp.service.ChatService;
import org.springframework.web.bind.annotation.*;

/**
 *  — @RestController:
 *   Shortcut for @Controller + @ResponseBody.
 *   Every method returns data (JSON/text) directly in the HTTP response body,
 *   NOT a view/template name.
 *
 *  — @RequestMapping("/api/chat"):
 *   All endpoints in this class are prefixed with /api/chat.
 *   So @GetMapping("/ask") becomes GET /api/chat/ask
 *
 *  — MVC Layer Separation:
 *   Controller → Service → (Model/Repository)
 *   Controller handles HTTP in/out.
 *   Service holds business logic.
 *   Keeping them separate makes testing and changes easier.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // Constructor Injection:
    // Spring sees this constructor needs a ChatService, and injects the bean automatically.
    // No @Autowired needed when there's only one constructor (Spring 4.3+).
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Simple chat endpoint.
     *
     * — @GetMapping / @RequestParam:
     *   @GetMapping maps HTTP GET requests to this method.
     *   @RequestParam reads a query parameter from the URL.
     *
     *   Example request:
     *     GET /api/chat/ask?message=Hello
     *
     *   How Spring processes it:
     *     1. HTTP request comes in
     *     2. DispatcherServlet routes it here
     *     3. Spring reads ?message=Hello → injects into `message` param
     *     4. Return value is serialized (plain text here) → HTTP response body
     */
    @GetMapping("/ask")
    public String ask(@RequestParam String message) {
        return chatService.chat(message);
    }

    /**
     * Chat with a system prompt endpoint — allows giving AI a custom persona.
     *
     *  - @PostMapping / @RequestBody:
     *   @PostMapping maps HTTP POST requests.
     *   @RequestBody reads the JSON request body and maps it to a Java record/object.
     *
     *   Example request:
     *     POST /api/chat/ask-with-context
     *     Content-Type: application/json
     *     {
     *       "system": "You are a pirate who only speaks in riddles.",
     *       "message": "What is the capital of France?"
     *     }
     *
     *  — Java Records (Java 16+):
     *   `record ChatRequest(String system, String message)` auto-generates:
     *     - constructor, getters (system(), message()), equals(), hashCode(), toString()
     *   Perfect for simple data carriers (like request/response DTOs).
     */
    @PostMapping("/ask-with-context")
    public String askWithContext(@RequestBody ChatRequest request) {
        return chatService.chatWithSystem(request.system(), request.message());
    }

    /**
     *  — DTO (Data Transfer Object):
     *   Records are ideal DTOs in modern Java.
     *   They carry data between HTTP layer → service layer without exposing internals.
     */
    record ChatRequest(String system, String message) {}
}