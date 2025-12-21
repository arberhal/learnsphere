package ch.zhaw.learnsphere.controller;

import java.util.List;

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

import ch.zhaw.learnsphere.model.Lesson;
import ch.zhaw.learnsphere.model.LessonCreateDTO;
import ch.zhaw.learnsphere.repository.CourseRepository;
import ch.zhaw.learnsphere.repository.LessonRepository;
import ch.zhaw.learnsphere.service.UserService;

@RestController
@RequestMapping("/api/teacher/courses/{courseId}/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public LessonController(
            LessonRepository lessonRepository,
            CourseRepository courseRepository,
            UserService userService) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createLesson(
            @PathVariable String courseId,
            @RequestBody LessonCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can create lessons");
        }

        String teacherSub = jwt.getSubject();
        if (!courseRepository.findById(courseId)
                .filter(course -> course.getTeacherSub().equals(teacherSub))
                .isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only create lessons for your own courses");
        }

        Lesson lesson = new Lesson(
                courseId,
                dto.getTitle(),
                dto.getContent(),
                dto.getVideoUrl(),
                dto.getOrder());

        return new ResponseEntity<>(
                lessonRepository.save(lesson),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Lesson>> getLessons(@PathVariable String courseId) {
        if (!courseRepository.existsById(courseId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                lessonRepository.findByCourseIdOrderByOrderAsc(courseId));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<Lesson> getLessonById(
            @PathVariable String courseId,
            @PathVariable String lessonId) {

        return lessonRepository.findById(lessonId)
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<?> updateLesson(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @RequestBody LessonCreateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can update lessons");
        }

        String teacherSub = jwt.getSubject();
        if (!courseRepository.findById(courseId)
                .filter(course -> course.getTeacherSub().equals(teacherSub))
                .isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only update lessons for your own courses");
        }

        return lessonRepository.findById(lessonId)
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .map(lesson -> {
                    lesson.setTitle(dto.getTitle());
                    lesson.setContent(dto.getContent());
                    lesson.setVideoUrl(dto.getVideoUrl());
                    lesson.setOrder(dto.getOrder());
                    return ResponseEntity.ok(lessonRepository.save(lesson));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<?> deleteLesson(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @AuthenticationPrincipal Jwt jwt) {

        if (!userService.isTeacher(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can delete lessons");
        }

        String teacherSub = jwt.getSubject();
        if (!courseRepository.findById(courseId)
                .filter(course -> course.getTeacherSub().equals(teacherSub))
                .isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only delete lessons from your own courses");
        }

        return lessonRepository.findById(lessonId)
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .map(lesson -> {
                    lessonRepository.deleteById(lessonId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
