# Spring Boot AI Chat App

A simple AI chat service built with **Spring Boot 3.4** and **Spring AI 1.0** on top of the OpenAI Chat API. It demonstrates clean MVC layering, the `ChatClient` fluent API, conversation memory, request validation, streaming responses, and centralized error handling.

## Features

- **Simple chat** — one-shot question/answer.
- **System-prompted chat** — give the AI a persona/context.
- **Streaming** — token-by-token responses over Server-Sent Events.
- **Conversation memory** — multi-turn context via a per-conversation sliding window.
- **Validation & error handling** — clean JSON errors instead of stack traces.
- **Actuator** — health/info endpoints for ops.

## Requirements

- JDK 23 (adjust `<java.version>` in `pom.xml` if you use a different JDK)
- An OpenAI API key

## Configuration

Copy `.env.example` to `.env` and set your key:

```properties
OPENAI_API_KEY=sk-your-key-here
```

The model is set in `src/main/resources/application.properties`:

```properties
spring.ai.openai.chat.options.model=gpt-4o-mini
```

Use a model id your key actually has access to.

## Run

```bash
./mvnw spring-boot:run
```

## API

| Method | Endpoint                     | Body / Params                                  | Description                     |
|--------|------------------------------|------------------------------------------------|---------------------------------|
| GET    | `/api/chat/ask`              | `message`, `conversationId?`                   | One-shot chat                   |
| POST   | `/api/chat/ask-with-context` | `{ "system": "...", "message": "..." }`        | Chat with a system persona      |
| GET    | `/api/chat/stream`           | `message`, `conversationId?`                    | Streaming (SSE) chat            |
| GET    | `/actuator/health`           | —                                              | Health check                    |

### Example

```bash
curl "http://localhost:8080/api/chat/ask?message=Hello&conversationId=demo"
```

## Test

```bash
./mvnw test
```

## License

MIT — see [LICENSE](LICENSE).
