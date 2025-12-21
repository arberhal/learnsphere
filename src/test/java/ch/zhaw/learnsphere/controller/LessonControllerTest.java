package ch.zhaw.learnsphere.controller;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.zhaw.learnsphere.model.Course;
import ch.zhaw.learnsphere.model.Lesson;
import ch.zhaw.learnsphere.repository.CourseRepository;
import ch.zhaw.learnsphere.repository.LessonRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    private String teacherCourseId;
    private String otherTeacherCourseId;
    private String lessonId;

    private static final String TEACHER_SUB = "teacher123";
    private static final String OTHER_TEACHER_SUB = "teacher456";
    private static final String STUDENT_SUB = "student123";

    @BeforeEach
    public void setUp() {
        // Clean up
        lessonRepository.deleteAll();
        courseRepository.deleteAll();

        // Create course for teacher
        Course teacherCourse = new Course();
        teacherCourse.setTeacherSub(TEACHER_SUB);
        teacherCourse.setTitle("Teacher's Course");
        teacherCourse.setDescription("Test course");
        teacherCourse = courseRepository.save(teacherCourse);
        teacherCourseId = teacherCourse.getId();

        // Create course for other teacher
        Course otherTeacherCourse = new Course();
        otherTeacherCourse.setTeacherSub(OTHER_TEACHER_SUB);
        otherTeacherCourse.setTitle("Other Teacher's Course");
        otherTeacherCourse.setDescription("Test course");
        otherTeacherCourse = courseRepository.save(otherTeacherCourse);
        otherTeacherCourseId = otherTeacherCourse.getId();

        // Create a lesson for testing
        Lesson lesson = new Lesson(
                teacherCourseId,
                "Test Lesson",
                "Test Content",
                "https://example.com/video",
                1);
        lesson = lessonRepository.save(lesson);
        lessonId = lesson.getId();
    }

    // POST /api/teacher/courses/{courseId}/lessons - Teacher can create lesson (201)
    @Test
    public void testTeacherCanCreateLesson() throws Exception {
        String jsonDto = """
                {
                    "title": "New Lesson",
                    "content": "New Content",
                    "videoUrl": "https://example.com/video2",
                    "order": 2
                }
                """;

        mockMvc.perform(post("/api/teacher/courses/" + teacherCourseId + "/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Lesson"))
                .andExpect(jsonPath("$.content").value("New Content"))
                .andExpect(jsonPath("$.videoUrl").value("https://example.com/video2"))
                .andExpect(jsonPath("$.order").value(2))
                .andExpect(jsonPath("$.courseId").value(teacherCourseId));
    }

    // POST /api/teacher/courses/{courseId}/lessons - Student cannot create lesson (403)
    @Test
    public void testStudentCannotCreateLesson() throws Exception {
        String jsonDto = """
                {
                    "title": "New Lesson",
                    "content": "New Content",
                    "videoUrl": "https://example.com/video2",
                    "order": 2
                }
                """;

        mockMvc.perform(post("/api/teacher/courses/" + teacherCourseId + "/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)
                .with(jwt().jwt(j -> j
                        .subject(STUDENT_SUB)
                        .claim("email", STUDENT_SUB + "@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Only teachers can create lessons"));
    }

    // POST /api/teacher/courses/{courseId}/lessons - Teacher cannot create lesson for other teacher's course (403)
    @Test
    public void testTeacherCannotCreateLessonForOtherTeachersCourse() throws Exception {
        String jsonDto = """
                {
                    "title": "New Lesson",
                    "content": "New Content",
                    "videoUrl": "https://example.com/video2",
                    "order": 2
                }
                """;

        mockMvc.perform(post("/api/teacher/courses/" + otherTeacherCourseId + "/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You can only create lessons for your own courses"));
    }

    // GET /api/teacher/courses/{courseId}/lessons - Returns all lessons ordered by order field (200)
    @Test
    public void testGetAllLessonsOrdered() throws Exception {
        // Create additional lessons with different orders
        Lesson lesson2 = new Lesson(teacherCourseId, "Lesson 2", "Content 2", "url2", 3);
        Lesson lesson3 = new Lesson(teacherCourseId, "Lesson 3", "Content 3", "url3", 2);
        lessonRepository.save(lesson2);
        lessonRepository.save(lesson3);

        mockMvc.perform(get("/api/teacher/courses/" + teacherCourseId + "/lessons")
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].order").value(1))
                .andExpect(jsonPath("$[1].order").value(2))
                .andExpect(jsonPath("$[2].order").value(3))
                .andExpect(jsonPath("$[0].title").value("Test Lesson"))
                .andExpect(jsonPath("$[1].title").value("Lesson 3"))
                .andExpect(jsonPath("$[2].title").value("Lesson 2"));
    }

    // GET /api/teacher/courses/{courseId}/lessons - Returns 404 if course doesn't exist
    @Test
    public void testGetLessonsForNonExistentCourse() throws Exception {
        mockMvc.perform(get("/api/teacher/courses/nonexistent123/lessons")
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNotFound());
    }

    // GET /api/teacher/courses/{courseId}/lessons/{lessonId} - Returns single lesson (200)
    @Test
    public void testGetSingleLesson() throws Exception {
        mockMvc.perform(get("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lessonId))
                .andExpect(jsonPath("$.title").value("Test Lesson"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.videoUrl").value("https://example.com/video"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.courseId").value(teacherCourseId));
    }

    // GET /api/teacher/courses/{courseId}/lessons/{lessonId} - Returns 404 if lesson doesn't belong to course
    @Test
    public void testGetLessonFromWrongCourse() throws Exception {
        mockMvc.perform(get("/api/teacher/courses/" + otherTeacherCourseId + "/lessons/" + lessonId)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNotFound());
    }

    // PUT /api/teacher/courses/{courseId}/lessons/{lessonId} - Teacher can update own lesson (200)
    @Test
    public void testTeacherCanUpdateOwnLesson() throws Exception {
        String jsonDto = """
                {
                    "title": "Updated Lesson",
                    "content": "Updated Content",
                    "videoUrl": "https://example.com/updated",
                    "order": 5
                }
                """;

        mockMvc.perform(put("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Lesson"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.videoUrl").value("https://example.com/updated"))
                .andExpect(jsonPath("$.order").value(5));
    }

    // PUT /api/teacher/courses/{courseId}/lessons/{lessonId} - Student cannot update lesson (403)
    @Test
    public void testStudentCannotUpdateLesson() throws Exception {
        String jsonDto = """
                {
                    "title": "Updated Lesson",
                    "content": "Updated Content",
                    "videoUrl": "https://example.com/updated",
                    "order": 5
                }
                """;

        mockMvc.perform(put("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)
                .with(jwt().jwt(j -> j
                        .subject(STUDENT_SUB)
                        .claim("email", STUDENT_SUB + "@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Only teachers can update lessons"));
    }

    // PUT /api/teacher/courses/{courseId}/lessons/{lessonId} - Teacher cannot update other teacher's lesson (403)
    @Test
    public void testTeacherCannotUpdateOtherTeachersLesson() throws Exception {
        String jsonDto = """
                {
                    "title": "Updated Lesson",
                    "content": "Updated Content",
                    "videoUrl": "https://example.com/updated",
                    "order": 5
                }
                """;

        mockMvc.perform(put("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto)
                .with(jwt().jwt(j -> j
                        .subject(OTHER_TEACHER_SUB)
                        .claim("email", OTHER_TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You can only update lessons for your own courses"));
    }

    // DELETE /api/teacher/courses/{courseId}/lessons/{lessonId} - Teacher can delete own lesson (204)
    @Test
    public void testTeacherCanDeleteOwnLesson() throws Exception {
        mockMvc.perform(delete("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNoContent());

        // Verify lesson was deleted
        mockMvc.perform(get("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isNotFound());
    }

    // DELETE /api/teacher/courses/{courseId}/lessons/{lessonId} - Student cannot delete lesson (403)
    @Test
    public void testStudentCannotDeleteLesson() throws Exception {
        mockMvc.perform(delete("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .with(jwt().jwt(j -> j
                        .subject(STUDENT_SUB)
                        .claim("email", STUDENT_SUB + "@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Only teachers can delete lessons"));
    }

    // DELETE /api/teacher/courses/{courseId}/lessons/{lessonId} - Teacher cannot delete other teacher's lesson (403)
    @Test
    public void testTeacherCannotDeleteOtherTeachersLesson() throws Exception {
        mockMvc.perform(delete("/api/teacher/courses/" + teacherCourseId + "/lessons/" + lessonId)
                .with(jwt().jwt(j -> j
                        .subject(OTHER_TEACHER_SUB)
                        .claim("email", OTHER_TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You can only delete lessons from your own courses"));
    }

    // Test lessons are returned in correct order (ascending by order field)
    @Test
    public void testLessonsReturnedInCorrectOrder() throws Exception {
        // Create lessons with specific orders
        lessonRepository.deleteAll();
        
        Lesson lesson1 = new Lesson(teacherCourseId, "Lesson Order 5", "Content", "url", 5);
        Lesson lesson2 = new Lesson(teacherCourseId, "Lesson Order 1", "Content", "url", 1);
        Lesson lesson3 = new Lesson(teacherCourseId, "Lesson Order 3", "Content", "url", 3);
        Lesson lesson4 = new Lesson(teacherCourseId, "Lesson Order 2", "Content", "url", 2);
        
        lessonRepository.save(lesson1);
        lessonRepository.save(lesson2);
        lessonRepository.save(lesson3);
        lessonRepository.save(lesson4);

        mockMvc.perform(get("/api/teacher/courses/" + teacherCourseId + "/lessons")
                .with(jwt().jwt(j -> j
                        .subject(TEACHER_SUB)
                        .claim("email", TEACHER_SUB + "@example.com")
                        .claim("user_roles", List.of("teacher")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].order").value(1))
                .andExpect(jsonPath("$[0].title").value("Lesson Order 1"))
                .andExpect(jsonPath("$[1].order").value(2))
                .andExpect(jsonPath("$[1].title").value("Lesson Order 2"))
                .andExpect(jsonPath("$[2].order").value(3))
                .andExpect(jsonPath("$[2].title").value("Lesson Order 3"))
                .andExpect(jsonPath("$[3].order").value(5))
                .andExpect(jsonPath("$[3].title").value("Lesson Order 5"));
    }
}