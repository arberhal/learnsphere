package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.zhaw.learnsphere.model.Course;
import ch.zhaw.learnsphere.model.Lesson;
import ch.zhaw.learnsphere.repository.CourseRepository;
import ch.zhaw.learnsphere.repository.LessonRepository;
import ch.zhaw.learnsphere.repository.ProgressRepository;
import ch.zhaw.learnsphere.security.TestSecurityConfig;

/**
 * INTEGRATION TESTS for ProgressController
 * 
 * ✅ Uses @SpringBootTest (loads full application)
 * ✅ Uses real MongoDB connection
 * ✅ Tests role-based authentication (Student vs Teacher)
 * ✅ Tests progress tracking functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ProgressRepository progressRepository;

    // Test data IDs
    private static String testCourseId;

    // JWT Claims für verschiedene Rollen
    private static final String STUDENT_SUB = "auth0|student123";
    private static final String TEACHER_SUB = "auth0|teacher123";

    @BeforeAll
    static void setup(@Autowired CourseRepository courseRepository,
                      @Autowired LessonRepository lessonRepository) {
        // Create test course
        Course course = new Course("Test Course", "Test Description", TEACHER_SUB);
        Course savedCourse = courseRepository.save(course);
        testCourseId = savedCourse.getId();

        // Create test lessons for this course
        Lesson lesson1 = new Lesson();
        lesson1.setCourseId(testCourseId);
        lesson1.setTitle("Lesson 1");
        lesson1.setOrder(1);
        lessonRepository.save(lesson1);

        Lesson lesson2 = new Lesson();
        lesson2.setCourseId(testCourseId);
        lesson2.setTitle("Lesson 2");
        lesson2.setOrder(2);
        lessonRepository.save(lesson2);

        Lesson lesson3 = new Lesson();
        lesson3.setCourseId(testCourseId);
        lesson3.setTitle("Lesson 3");
        lesson3.setOrder(3);
        lessonRepository.save(lesson3);

        System.out.println("Created test course with ID: " + testCourseId);
    }

    @AfterAll
    static void cleanup(@Autowired CourseRepository courseRepository,
                        @Autowired LessonRepository lessonRepository,
                        @Autowired ProgressRepository progressRepository) {
        // Cleanup test data
        if (testCourseId != null) {
            // Delete lessons individually
            lessonRepository.findByCourseIdOrderByOrderAsc(testCourseId)
                    .forEach(lesson -> lessonRepository.deleteById(lesson.getId()));
            courseRepository.deleteById(testCourseId);
        }
        // Also cleanup any progress for our test student
        progressRepository.findByStudentSub(STUDENT_SUB)
                .forEach(p -> progressRepository.deleteById(p.getId()));
    }

    // ========================================
    // TEST 1: Student can enroll (create progress with 0 lessons)
    // ========================================
    @Test
    @Order(1)
    void testStudentCanEnroll() throws Exception {
        mockMvc.perform(post("/api/student/progress/" + testCourseId + "/0")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(testCourseId))
                .andExpect(jsonPath("$.studentSub").value(STUDENT_SUB))
                .andExpect(jsonPath("$.completedLessons").value(0))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    // ========================================
    // TEST 2: Teacher cannot enroll (403)
    // ========================================
    @Test
    @Order(2)
    void testTeacherCannotEnroll() throws Exception {
        mockMvc.perform(post("/api/student/progress/" + testCourseId + "/0")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isForbidden());
    }

    // ========================================
    // TEST 3: Student can complete lesson (update progress)
    // ========================================
    @Test
    @Order(3)
    void testStudentCanCompleteLesson() throws Exception {
        mockMvc.perform(post("/api/student/progress/" + testCourseId + "/1")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(testCourseId))
                .andExpect(jsonPath("$.completedLessons").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    // ========================================
    // TEST 4: Teacher cannot complete lesson (403)
    // ========================================
    @Test
    @Order(4)
    void testTeacherCannotCompleteLesson() throws Exception {
        mockMvc.perform(post("/api/student/progress/" + testCourseId + "/1")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isForbidden());
    }

    // ========================================
    // TEST 5: GET returns correct progress data
    // ========================================
    @Test
    @Order(5)
    void testGetProgressReturnsCorrectData() throws Exception {
        mockMvc.perform(get("/api/student/progress/" + testCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(testCourseId))
                .andExpect(jsonPath("$.studentSub").value(STUDENT_SUB))
                .andExpect(jsonPath("$.completedLessons").exists())
                .andExpect(jsonPath("$.percent").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    // ========================================
    // TEST 6: Progress status updates correctly (complete all lessons)
    // ========================================
    @Test
    @Order(6)
    void testProgressStatusUpdatesCorrectly() throws Exception {
        // Complete all 3 lessons
        mockMvc.perform(post("/api/student/progress/" + testCourseId + "/3")
                .with(jwt().jwt(builder -> builder
                        .claim("sub", STUDENT_SUB)
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedLessons").value(3))
                .andExpect(jsonPath("$.percent").value(100.0))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    // ========================================
    // TEST 7: Teacher cannot view student progress (403)
    // ========================================
    @Test
    @Order(7)
    void testTeacherCannotViewProgress() throws Exception {
        mockMvc.perform(get("/api/student/progress/" + testCourseId)
                .with(jwt().jwt(builder -> builder
                        .claim("sub", TEACHER_SUB)
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isForbidden());
    }
}