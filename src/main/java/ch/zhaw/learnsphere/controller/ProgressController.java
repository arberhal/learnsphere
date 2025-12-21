package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.learnsphere.model.Progress;
import ch.zhaw.learnsphere.model.ProgressStatus;
import ch.zhaw.learnsphere.repository.LessonRepository;
import ch.zhaw.learnsphere.repository.ProgressRepository;
import ch.zhaw.learnsphere.service.UserService;

@RestController
@RequestMapping("/api/student/progress")
public class ProgressController {

    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final UserService userService;

    public ProgressController(
            ProgressRepository progressRepository,
            LessonRepository lessonRepository,
            UserService userService) {
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
        this.userService = userService;
    }

    @PostMapping("/{courseId}/{completedLessons}")
    public ResponseEntity<?> updateProgress(
            @PathVariable String courseId,
            @PathVariable Integer completedLessons,
            @AuthenticationPrincipal Jwt jwt) {

        if (!userService.isStudent(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only students can update progress");
        }

        String studentSub = jwt.getSubject();

        int totalLessons = lessonRepository
                .findByCourseIdOrderByOrderAsc(courseId)
                .size();

        int safeCompletedLessons = Math.min(completedLessons, totalLessons);

        double percent = totalLessons == 0
                ? 0
                : (safeCompletedLessons * 100.0) / totalLessons;

        Progress progress = progressRepository
                .findByCourseIdAndStudentSub(courseId, studentSub)
                .orElse(new Progress(null, courseId, studentSub, 0, 0.0));

        progress.setCompletedLessons(safeCompletedLessons);
        progress.setPercent(Math.min(percent, 100.0));

        progress.updateStatus(totalLessons);

        return ResponseEntity.ok(progressRepository.save(progress));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getProgress(
            @PathVariable String courseId,
            @AuthenticationPrincipal Jwt jwt) {

        if (!userService.isStudent(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only students can view progress");
        }

        String studentSub = jwt.getSubject();

        return progressRepository
                .findByCourseIdAndStudentSub(courseId, studentSub)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getAllProgress(@AuthenticationPrincipal Jwt jwt) {
        if (!userService.isStudent(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only students can view progress");
        }

        String studentSub = jwt.getSubject();
        List<Progress> progressList = progressRepository.findByStudentSub(studentSub);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getProgressByStatus(
            @PathVariable ProgressStatus status,
            @AuthenticationPrincipal Jwt jwt) {
        
        if (!userService.isStudent(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only students can view progress");
        }

        String studentSub = jwt.getSubject();
        List<Progress> progressList = progressRepository
                .findByStudentSubAndStatus(studentSub, status);
        return ResponseEntity.ok(progressList);
    }
}
