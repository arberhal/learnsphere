package ch.zhaw.learnsphere;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import ch.zhaw.learnsphere.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://test.auth0.com/",
    "spring.ai.openai.api-key=test-key",
    "spring.data.mongodb.uri=mongodb://localhost:27017/test"
})
class LearnsphereApplicationTests {

    @Test
    void contextLoads() {
    }
}