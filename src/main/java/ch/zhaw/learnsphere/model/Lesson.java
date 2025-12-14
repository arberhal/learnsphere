package ch.zhaw.learnsphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Document("lesson")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Lesson {

    @Id
    private String id;

    @NonNull
    private String courseId;

    @NonNull
    private String title;

    @NonNull
    private String content;

    @NonNull
    private String videoUrl;

    @NonNull
    private Integer order;
}
