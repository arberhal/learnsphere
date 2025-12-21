package ch.zhaw.learnsphere.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    
    public boolean userHasRole(Jwt jwt, String role) {
        if (jwt == null) {
            return false;
        }
        
        List<String> roles = jwt.getClaimAsStringList("user_roles");
        
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        
        return roles.contains(role);
    }
    
    
    public boolean isTeacher(Jwt jwt) {
        return userHasRole(jwt, "teacher");
    }
    
    
    public boolean isStudent(Jwt jwt) {
        return userHasRole(jwt, "student");
    }
}
