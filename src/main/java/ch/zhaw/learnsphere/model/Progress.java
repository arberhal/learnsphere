package ch.zhaw.learnsphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Document("progress")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Progress {

    @Id
    private String id;

    @NonNull
    private String courseId;

    @NonNull
    private String studentSub;

    @NonNull
    private Integer completedLessons;

    @NonNull
    private Double percent;
}
