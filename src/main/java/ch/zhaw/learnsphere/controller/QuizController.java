package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.learnsphere.model.Lesson;
import ch.zhaw.learnsphere.model.QuizQuestion;
import ch.zhaw.learnsphere.repository.LessonRepository;
import ch.zhaw.learnsphere.service.QuizService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private LessonRepository lessonRepository;

    @GetMapping("/lessons/{lessonId}/quiz")
    public ResponseEntity<?> generateQuiz(@PathVariable String lessonId) {
        
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (lesson.getContent() == null || lesson.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Lesson has no content to generate quiz from");
        }
        
        List<QuizQuestion> quiz = quizService.generateQuiz(lesson.getContent());
        
        if (quiz.isEmpty()) {
            return ResponseEntity.status(500)
                    .body("Failed to generate quiz. Please try again.");
        }
        
        return ResponseEntity.ok(quiz);
    }
}
