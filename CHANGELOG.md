# Changelog

All notable changes to this project are documented here.
The format is based on [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Added
- Conversation memory (per-`conversationId` sliding window) for multi-turn chat.
- Streaming chat endpoint (`GET /api/chat/stream`) over Server-Sent Events.
- Endpoint to clear a conversation's memory (`DELETE /api/chat/conversation/{id}`).
- Request validation (`@NotBlank`, `@Size`) and a global JSON error handler.
- Actuator health/info endpoints.
- CORS configuration for `/api/**`.
- Static HTML page to try chat and streaming in the browser.
- Swagger-free API reference in the README, plus `.env.example`, LICENSE, Dockerfile, and CI.
- Controller slice tests.

### Changed
- Migrated the service from `ChatModel` to the `ChatClient` fluent API.
- Responses are now wrapped in a JSON DTO instead of raw strings.

### Fixed
- Replaced the invalid `gpt-5.4` model id with a valid one.
- Aligned `<java.version>` with the installed JDK so the project builds.
