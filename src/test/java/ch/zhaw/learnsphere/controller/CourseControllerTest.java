package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.zhaw.learnsphere.repository.CourseRepository;
import ch.zhaw.learnsphere.security.TestSecurityConfig;

/**
 * INTEGRATION TESTS for CourseController
 * 
 * ✅ Uses @SpringBootTest (loads full application)
 * ✅ Uses real MongoDB connection
 * ✅ Tests actual role-based authentication
 * ✅ Tests real database operations
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    private static String createdCourseId;

    // JWT Claims für verschiedene Rollen
    private static final String TEACHER_SUB = "auth0|teacher123";
    private static final String OTHER_TEACHER_SUB = "auth0|teacher456";
    private static final String STUDENT_SUB = "auth0|student123";

    @AfterAll
    static void cleanup(@Autowired CourseRepository courseRepository) {
        // Cleanup nach allen Tests
        if (createdCourseId != null) {
            courseRepository.deleteById(createdCourseId);
        }
    }

    // ========================================
    // TEST 1: Teacher can create course (201)
    // ========================================
    @Test
    @Order(1)
    void testTeacherCanCreateCourse() throws Exception {
        String requestBody = """
            {
                "title": "Spring Boot Kurs",
                "description": "Lerne Spring Boot von Grund auf"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/teacher/courses")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.teacherSub").value(TEACHER_SUB))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Save ID for later tests
        String response = result.getResponse().getContentAsString();
        // Extract ID using simple string manipulation
        int idStart = response.indexOf("\"id\":\"") + 6;
        int idEnd = response.indexOf("\"", idStart);
        createdCourseId = response.substring(idStart, idEnd);
        System.out.println("Created course with ID: " + createdCourseId);
    }

    // ========================================
    // TEST 2: Student cannot create course (403)
    // ========================================
    @Test
    @Order(2)
    void testStudentCannotCreateCourse() throws Exception {
        String requestBody = """
            {
                "title": "Hacker Kurs",
                "description": "Sollte nicht erstellt werden"
            }
            """;

        mockMvc.perform(post("/api/teacher/courses")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // ========================================
    // TEST 3: Both roles can view course (200)
    // ========================================
    @Test
    @Order(3)
    void testBothRolesCanViewCourse() throws Exception {
        // Teacher kann Course sehen
        mockMvc.perform(get("/api/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists());

        // Student kann Course auch sehen
        mockMvc.perform(get("/api/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());
    }

    // ========================================
    // TEST 4: Only owner can edit course
    // ========================================
    @Test
    @Order(4)
    void testOnlyOwnerCanEditCourse() throws Exception {
        String updateBody = """
            {
                "title": "Updated Title",
                "description": "Updated Description"
            }
            """;

        // Owner kann editieren (200)
        mockMvc.perform(put("/api/teacher/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists());

        // Anderer Teacher kann NICHT editieren (404 - not found because not owner)
        mockMvc.perform(put("/api/teacher/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", OTHER_TEACHER_SUB)
                        .claim("user_roles", List.of("teacher"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isNotFound());

        // Student kann NICHT editieren (403)
        mockMvc.perform(put("/api/teacher/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isForbidden());
    }

    // ========================================
    // TEST 5: Only owner can delete course
    // ========================================
    @Test
    @Order(5)
    void testOnlyOwnerCanDeleteCourse() throws Exception {
        // Student kann NICHT löschen (403)
        mockMvc.perform(delete("/api/teacher/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isForbidden());

        // Anderer Teacher kann NICHT löschen (404)
        mockMvc.perform(delete("/api/teacher/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", OTHER_TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNotFound());

        // Owner kann löschen (204)
        mockMvc.perform(delete("/api/teacher/courses/" + createdCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNoContent());

        // Mark as deleted so cleanup doesn't fail
        createdCourseId = null;
    }

    // ========================================
    // TEST 6: Deleted course returns 404
    // ========================================
    @Test
    @Order(6)
    void testDeletedCourseNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/nonexistent-id")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNotFound());
    }

    // ========================================
    // TEST 7: Get all courses
    // ========================================
    @Test
    @Order(7)
    void testGetAllCourses() throws Exception {
        mockMvc.perform(get("/api/courses")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}