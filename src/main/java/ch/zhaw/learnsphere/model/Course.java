package ch.zhaw.learnsphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Document("course")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Course {

    @Id
    private String id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String teacherSub;
}
