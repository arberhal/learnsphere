package ch.zhaw.learnsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.learnsphere.model.Progress;
import ch.zhaw.learnsphere.repository.LessonRepository;
import ch.zhaw.learnsphere.repository.ProgressRepository;

@RestController
@RequestMapping("/api/student/progress")
public class ProgressController {

    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;

    public ProgressController(
            ProgressRepository progressRepository,
            LessonRepository lessonRepository) {
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
    }

    @PostMapping("/{courseId}/{completedLessons}")
    public ResponseEntity<Progress> updateProgress(
            @PathVariable String courseId,
            @PathVariable Integer completedLessons,
            @AuthenticationPrincipal Jwt jwt) {  // ✅ Get JWT

        // ✅ Extract real student's sub from JWT
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

        return ResponseEntity.ok(progressRepository.save(progress));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Progress> getProgress(
            @PathVariable String courseId,
            @AuthenticationPrincipal Jwt jwt) {  // ✅ Get JWT

        // ✅ Extract real student's sub from JWT
        String studentSub = jwt.getSubject();

        return progressRepository
                .findByCourseIdAndStudentSub(courseId, studentSub)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}