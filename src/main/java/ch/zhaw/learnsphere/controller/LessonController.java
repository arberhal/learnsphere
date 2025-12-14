package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.learnsphere.model.Lesson;
import ch.zhaw.learnsphere.model.LessonCreateDTO;
import ch.zhaw.learnsphere.repository.LessonRepository;

@RestController
@RequestMapping("/api/teacher/courses/{courseId}/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;

    public LessonController(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
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
        return ResponseEntity.ok(
            lessonRepository.findByCourseIdOrderByOrderAsc(courseId)
        );
    }
}
