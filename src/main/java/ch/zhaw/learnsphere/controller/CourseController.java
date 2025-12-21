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

    
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(
            @AuthenticationPrincipal Jwt jwt) {
        
        String studentSub = jwt.getSubject();
        
        List<String> courseIdsWithProgress = progressRepository
                .findByStudentSub(studentSub)
                .stream()
                .map(progress -> progress.getCourseId())
                .collect(Collectors.toList());
        
        List<Course> myCourses = courseRepository
                .findAllById(courseIdsWithProgress);
        
        return ResponseEntity.ok(myCourses);
    }

  
    @PostMapping("/teacher/courses")
    public ResponseEntity<?> createCourse(
            @RequestBody CourseCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only teachers can create courses");
        }

        String teacherSub = jwt.getSubject();
        
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
            
            String aiTitle = chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content()
                    .trim();
            
            aiTitle = aiTitle.replaceAll("^\"|\"$", "").trim();
            
            if (aiTitle.length() > 0 && aiTitle.length() <= 80) {
                improvedTitle = aiTitle;
            }
            
        } catch (Exception e) {
            System.err.println("AI title generation failed, using original: " + e.getMessage());
        }
        
        Course course = new Course(
                improvedTitle,
                dto.getDescription(),
                teacherSub
        );
        Course saved = courseRepository.save(course);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    
    @GetMapping("/teacher/courses")
    public ResponseEntity<List<Course>> getTeacherCourses(
            @AuthenticationPrincipal Jwt jwt) {
        
        String teacherSub = jwt.getSubject();
        
        return ResponseEntity.ok(
                courseRepository.findByTeacherSub(teacherSub)
        );
    }

    
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

    
    @PutMapping("/teacher/courses/{courseId}")
    public ResponseEntity<?> updateCourse(
            @PathVariable String courseId,
            @RequestBody CourseUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

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

    
    @DeleteMapping("/teacher/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable String courseId,
            @AuthenticationPrincipal Jwt jwt) {

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
