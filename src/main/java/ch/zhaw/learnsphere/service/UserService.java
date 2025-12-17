package ch.zhaw.learnsphere.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    /**
     * Check if user has a specific role
     * @param jwt The JWT token from the authenticated user
     * @param role The role to check (e.g., "teacher", "student")
     * @return true if user has the role, false otherwise
     */
    public boolean userHasRole(Jwt jwt, String role) {
        if (jwt == null) {
            return false;
        }
        
        // Get user_roles claim from JWT
        List<String> roles = jwt.getClaimAsStringList("user_roles");
        
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        
        return roles.contains(role);
    }
    
    /**
     * Check if user is a teacher
     * @param jwt The JWT token from the authenticated user
     * @return true if user is a teacher
     */
    public boolean isTeacher(Jwt jwt) {
        return userHasRole(jwt, "teacher");
    }
    
    /**
     * Check if user is a student
     * @param jwt The JWT token from the authenticated user
     * @return true if user is a student
     */
    public boolean isStudent(Jwt jwt) {
        return userHasRole(jwt, "student");
    }
}