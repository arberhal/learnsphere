package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/teacher/courses/{courseId}/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonController(LessonRepository lessonRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(
            @PathVariable String courseId,
            @RequestBody LessonCreateDTO dto) {

        Lesson lesson = new Lesson(
                courseId,
                dto.getTitle(),
                dto.getContent(),
                dto.getVideoUrl(),
                dto.getOrder()
        );

        return new ResponseEntity<>(
                lessonRepository.save(lesson),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Lesson>> getLessons(@PathVariable String courseId) {
        // Optionaler Course-Existenzcheck (empfohlen)
        if (!courseRepository.existsById(courseId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                lessonRepository.findByCourseIdOrderByOrderAsc(courseId)
        );
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @RequestBody LessonCreateDTO dto) {

        // optionaler Course-Check (empfohlen)
        if (!courseRepository.existsById(courseId)) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<Void> deleteLesson(
            @PathVariable String courseId,
            @PathVariable String lessonId) {

        return lessonRepository.findById(lessonId)
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .map(lesson -> {
                    lessonRepository.deleteById(lessonId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

}
