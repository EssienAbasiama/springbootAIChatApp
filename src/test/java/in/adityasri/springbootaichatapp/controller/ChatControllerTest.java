package in.adityasri.springbootaichatapp.controller;

import in.adityasri.springbootaichatapp.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * — @WebMvcTest:
 *   Loads ONLY the web layer (this controller + the @RestControllerAdvice),
 *   not the full application context. Fast, and no real OpenAI call is made.
 *
 * — @MockitoBean:
 *   Replaces the real ChatService bean with a Mockito mock so we can drive
 *   controller behaviour (routing, validation, JSON shape) in isolation.
 *   (@MockBean is deprecated as of Spring Boot 3.4.)
 */
@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void ask_returnsAnswerAsJson() throws Exception {
        when(chatService.chat(anyString(), eq("Hello"))).thenReturn("Hi there!");

        mockMvc.perform(get("/api/chat/ask").param("message", "Hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Hi there!"));
    }

    @Test
    void ask_blankMessage_returns400() throws Exception {
        mockMvc.perform(get("/api/chat/ask").param("message", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ask_missingMessage_returns400() throws Exception {
        mockMvc.perform(get("/api/chat/ask"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void askWithContext_validBody_returnsAnswer() throws Exception {
        when(chatService.chatWithSystem(anyString(), eq("You are a pirate."), eq("Hello")))
                .thenReturn("Arrr, ahoy!");

        mockMvc.perform(post("/api/chat/ask-with-context")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"system\":\"You are a pirate.\",\"message\":\"Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Arrr, ahoy!"));
    }

    @Test
    void askWithContext_blankField_returns400() throws Exception {
        mockMvc.perform(post("/api/chat/ask-with-context")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"system\":\"\",\"message\":\"Hello\"}"))
                .andExpect(status().isBadRequest());
    }
}
