/**
 * Spring Boot AI Chat App.
 *
 * <p>A small REST service that talks to an OpenAI chat model through Spring AI's
 * {@code ChatClient}. It is organized into the conventional layers:
 *
 * <ul>
 *   <li>{@code controller} — HTTP endpoints, request validation, error handling</li>
 *   <li>{@code service} — chat business logic and conversation memory</li>
 *   <li>{@code config} — CORS and chat-memory beans</li>
 * </ul>
 */
package in.adityasri.springbootaichatapp;
