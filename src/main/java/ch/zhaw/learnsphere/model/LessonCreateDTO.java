package ch.zhaw.learnsphere.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LessonCreateDTO {
    private String title;
    private String content;
    private String videoUrl;
    private Integer order;
}
