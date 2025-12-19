package ch.zhaw.learnsphere.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.learnsphere.model.Course;
import ch.zhaw.learnsphere.model.CourseCreateDTO;
import ch.zhaw.learnsphere.model.CourseUpdateDTO;
import ch.zhaw.learnsphere.repository.CourseRepository;
import ch.zhaw.learnsphere.repository.ProgressRepository;
import ch.zhaw.learnsphere.service.UserService;

@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseRepository courseRepository;
    private final ProgressRepository progressRepository;
    private final UserService userService;

    @Autowired
    private ChatClient chatClient;

    public CourseController(
            CourseRepository courseRepository, 
            ProgressRepository progressRepository,
            UserService userService) {
        this.courseRepository = courseRepository;
        this.progressRepository = progressRepository;
        this.userService = userService;
    }

    // ========================================
    // PUBLIC ENDPOINTS (All authenticated users)
    // ========================================

    /**
     * GET /api/courses
     * Returns ALL available courses (for browsing)
     */
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    /**
     * GET /api/courses/{id}
     * Returns a single course by ID
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/my-courses
     * Returns only courses where the current user has made progress
     */
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(
            @AuthenticationPrincipal Jwt jwt) {
        
        String studentSub = jwt.getSubject();
        
        // Get all progress records for this user
        List<String> courseIdsWithProgress = progressRepository
                .findByStudentSub(studentSub)
                .stream()
                .map(progress -> progress.getCourseId())
                .collect(Collectors.toList());
        
        // Get courses for those IDs
        List<Course> myCourses = courseRepository
                .findAllById(courseIdsWithProgress);
        
        return ResponseEntity.ok(myCourses);
    }

    // ========================================
    // TEACHER ENDPOINTS (Teacher role required)
    // ========================================

    /**
     * POST /api/teacher/courses
     * Create a new course (Teachers only)
     * ✨ WITH AI TITLE GENERATION ✨
     */
    @PostMapping("/teacher/courses")
    public ResponseEntity<?> createCourse(
            @RequestBody CourseCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        // ✅ Check if user is a teacher
        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only teachers can create courses");
        }

        String teacherSub = jwt.getSubject();
        
        // ✨ AI TITLE GENERATION ✨
        String improvedTitle = dto.getTitle();
        try {
            String promptText = String.format(
                "Improve this course title: '%s'. " +
                "Based on this description: %s. " +
                "Return ONLY the improved title, nothing else. " +
                "Keep it concise (max 60 characters) and professional.",
                dto.getTitle(), 
                dto.getDescription()
            );
            
            // Use ChatClient
            String aiTitle = chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content()
                    .trim();
            
            // Remove quotes if AI added them
            aiTitle = aiTitle.replaceAll("^\"|\"$", "").trim();
            
            // Only use AI title if it's reasonable length
            if (aiTitle.length() > 0 && aiTitle.length() <= 80) {
                improvedTitle = aiTitle;
            }
            
        } catch (Exception e) {
            // If AI fails, use original title (silent fallback)
            System.err.println("AI title generation failed, using original: " + e.getMessage());
        }
        
        Course course = new Course(
                improvedTitle,  // ← Use AI-improved title
                dto.getDescription(),
                teacherSub
        );
        Course saved = courseRepository.save(course);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * GET /api/teacher/courses
     * Get courses created by the authenticated teacher
     */
    @GetMapping("/teacher/courses")
    public ResponseEntity<List<Course>> getTeacherCourses(
            @AuthenticationPrincipal Jwt jwt) {
        
        String teacherSub = jwt.getSubject();
        
        return ResponseEntity.ok(
                courseRepository.findByTeacherSub(teacherSub)
        );
    }

    /**
     * GET /api/teacher/courses/{courseId}
     * Get a specific course (only if it belongs to the teacher)
     */
    @GetMapping("/teacher/courses/{courseId}")
    public ResponseEntity<Course> getTeacherCourseById(
            @PathVariable String courseId,
            @AuthenticationPrincipal Jwt jwt) {
        
        String teacherSub = jwt.getSubject();
        
        return courseRepository.findById(courseId)
                .filter(course -> course.getTeacherSub().equals(teacherSub))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/teacher/courses/{courseId}
     * Update a course (Teachers only, own courses only)
     */
    @PutMapping("/teacher/courses/{courseId}")
    public ResponseEntity<?> updateCourse(
            @PathVariable String courseId,
            @RequestBody CourseUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        // ✅ Check if user is a teacher
        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only teachers can update courses");
        }

        String teacherSub = jwt.getSubject();

        return courseRepository.findById(courseId)
                .filter(course -> course.getTeacherSub().equals(teacherSub))
                .map(course -> {
                    course.setTitle(dto.getTitle());
                    course.setDescription(dto.getDescription());
                    return ResponseEntity.ok(courseRepository.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/teacher/courses/{courseId}
     * Delete a course (Teachers only, own courses only)
     */
    @DeleteMapping("/teacher/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable String courseId,
            @AuthenticationPrincipal Jwt jwt) {

        // ✅ Check if user is a teacher
        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only teachers can delete courses");
        }

        String teacherSub = jwt.getSubject();

        return courseRepository.findById(courseId)
                .filter(course -> course.getTeacherSub().equals(teacherSub))
                .map(course -> {
                    courseRepository.deleteById(courseId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}