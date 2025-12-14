package ch.zhaw.learnsphere.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CourseCreateDTO {
    private String title;
    private String description;
}
