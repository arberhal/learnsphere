package ch.zhaw.learnsphere.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctAnswer; // Index of correct option (0-3)
}