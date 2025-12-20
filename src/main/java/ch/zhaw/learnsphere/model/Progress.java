package ch.zhaw.learnsphere.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Progress {

    @Id
    private String id;

    private String courseId;
    private String studentSub;
    private int completedLessons;
    private double percent;
    
    // ✨ Status field for state tracking (3 states)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;
    
    // ✨ Timestamps for tracking progress timeline
    private LocalDateTime startedAt;  // When first lesson was completed
    private LocalDateTime completedAt;  // When all lessons were completed
    
    /**
     * Constructor without status (for backward compatibility)
     */
    public Progress(String id, String courseId, String studentSub, int completedLessons, double percent) {
        this.id = id;
        this.courseId = courseId;
        this.studentSub = studentSub;
        this.completedLessons = completedLessons;
        this.percent = percent;
        this.status = ProgressStatus.NOT_STARTED;
    }
    
    /**
     * Update status based on completed lessons and total lessons
     * Implements state transition logic (3 states)
     * 
     * NOT_STARTED → IN_PROGRESS → COMPLETED
     */
    public void updateStatus(int totalLessons) {
        if (completedLessons == 0) {
            // Not started yet
            this.status = ProgressStatus.NOT_STARTED;
            this.startedAt = null;
            this.completedAt = null;
        } else if (completedLessons >= totalLessons) {
            // All lessons completed
            this.status = ProgressStatus.COMPLETED;
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now();
            }
        } else {
            // In progress (at least one lesson completed)
            this.status = ProgressStatus.IN_PROGRESS;
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
            this.completedAt = null;
        }
    }
}