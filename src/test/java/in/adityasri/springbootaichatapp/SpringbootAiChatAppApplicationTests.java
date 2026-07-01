package in.adityasri.springbootaichatapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * LEARNING NOTE — @SpringBootTest:
 *   Loads the FULL application context for integration tests.
 *   Slower than unit tests but verifies the whole wiring is correct.
 *
 * LEARNING NOTE — @TestPropertySource:
 *   Overrides properties during tests.
 *   Here we set a dummy API key so Spring AI doesn't fail to start
 *   in CI/CD or when running tests without a real .env file.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.ai.openai.api-key=test-key",
        "spring.config.import="        // disable .env import during tests
})
class SpringbootAiChatAppApplicationTests {

    @Test
    void contextLoads() {
        // If this passes, all beans wired correctly — DI works, config is valid.
    }

}