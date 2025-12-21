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
    
    private ProgressStatus status = ProgressStatus.NOT_STARTED;
    
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    
    public Progress(String id, String courseId, String studentSub, int completedLessons, double percent) {
        this.id = id;
        this.courseId = courseId;
        this.studentSub = studentSub;
        this.completedLessons = completedLessons;
        this.percent = percent;
        this.status = ProgressStatus.NOT_STARTED;
    }
    
    
    public void updateStatus(int totalLessons) {
        if (completedLessons == 0) {
            this.status = ProgressStatus.NOT_STARTED;
            this.startedAt = null;
            this.completedAt = null;
        } else if (completedLessons >= totalLessons) {
            this.status = ProgressStatus.COMPLETED;
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now();
            }
        } else {
            this.status = ProgressStatus.IN_PROGRESS;
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
            this.completedAt = null;
        }
    }
}
