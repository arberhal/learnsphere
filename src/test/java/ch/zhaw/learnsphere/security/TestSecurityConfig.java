package ch.zhaw.learnsphere.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestSecurityConfig {
    
    // Mock JWT Tokens für Tests
    public static final String TEACHER = "Bearer teacher";
    public static final String STUDENT = "Bearer student";
    public static final String OTHER_TEACHER = "Bearer other-teacher";
    public static final String INVALID = "Bearer invalid";
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) {
                var bearer = "Bearer " + token;
                
                if (bearer.equals(TEACHER)) {
                    return createJwt("test-user-teacher", List.of("teacher"));
                }
                if (bearer.equals(STUDENT)) {
                    return createJwt("test-user-student", List.of("student"));
                }
                if (bearer.equals(OTHER_TEACHER)) {
                    return createJwt("other-teacher-sub", List.of("teacher"));
                }
                
                throw new AuthenticationException("Invalid JWT") {};
            }
        };
    }
    
    /**
     * Erstellt ein JWT mit den angegebenen Claims
     * WICHTIG: user_roles muss eine Liste sein, da UserService.userHasRole() 
     * jwt.getClaimAsStringList("user_roles") aufruft
     */
    private Jwt createJwt(String subject, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("user_roles", roles);  // Als Liste, nicht als String!
        claims.put("email", subject + "@learnsphere.com");
        
        return new Jwt(
            "valid-token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "none"),
            claims
        );
    }
}