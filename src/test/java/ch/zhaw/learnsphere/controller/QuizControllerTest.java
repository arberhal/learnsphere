package ch.zhaw.learnsphere.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.learnsphere.model.Lesson;
import ch.zhaw.learnsphere.model.QuizQuestion;
import ch.zhaw.learnsphere.repository.LessonRepository;
import ch.zhaw.learnsphere.service.QuizService;

@SpringBootTest
@AutoConfigureMockMvc
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LessonRepository lessonRepository;

    @MockBean
    private QuizService quizService;

    private String lessonId;
    private Lesson testLesson;

    @BeforeEach
    public void setUp() {
        // Clean up
        lessonRepository.deleteAll();

        // Create a test lesson with content
        testLesson = new Lesson(
                "course123",
                "Test Lesson",
                "Java is a programming language. It is object-oriented and platform-independent. Java applications are compiled to bytecode.",
                "https://example.com/video",
                1);
        testLesson = lessonRepository.save(testLesson);
        lessonId = testLesson.getId();
    }

    // GET /api/lessons/{lessonId}/quiz - Successfully generates quiz for valid lesson (200)
    @Test
    public void testGenerateQuizSuccessfully() throws Exception {
        // Mock quiz service to return sample questions
        List<QuizQuestion> mockQuiz = createMockQuiz();
        when(quizService.generateQuiz(anyString())).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/lessons/" + lessonId + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].question").exists())
                .andExpect(jsonPath("$[0].options").isArray())
                .andExpect(jsonPath("$[0].options", hasSize(4)))
                .andExpect(jsonPath("$[0].correctAnswer").exists());
    }

    // GET /api/lessons/{lessonId}/quiz - Returns 404 when lesson doesn't exist
    @Test
    public void testGenerateQuizLessonNotFound() throws Exception {
        mockMvc.perform(get("/api/lessons/nonexistent123/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isNotFound());
    }

    // GET /api/lessons/{lessonId}/quiz - Returns 400 when lesson has no content
    @Test
    public void testGenerateQuizNoContent() throws Exception {
        // Create lesson with empty string content (since null is not allowed)
        Lesson emptyLesson = new Lesson(
                "course123",
                "Empty Lesson",
                "",
                "https://example.com/video",
                2);
        emptyLesson = lessonRepository.save(emptyLesson);

        mockMvc.perform(get("/api/lessons/" + emptyLesson.getId() + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lesson has no content to generate quiz from"));
    }

    // GET /api/lessons/{lessonId}/quiz - Returns 400 when lesson content is empty/whitespace only
    @Test
    public void testGenerateQuizEmptyContent() throws Exception {
        // Create lesson with whitespace-only content
        Lesson whitespaceLesson = new Lesson(
                "course123",
                "Whitespace Lesson",
                "   ",
                "https://example.com/video",
                3);
        whitespaceLesson = lessonRepository.save(whitespaceLesson);

        mockMvc.perform(get("/api/lessons/" + whitespaceLesson.getId() + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lesson has no content to generate quiz from"));
    }

    // GET /api/lessons/{lessonId}/quiz - Returns 500 when quiz generation fails
    @Test
    public void testGenerateQuizServiceFailure() throws Exception {
        // Mock quiz service to return empty list (failure)
        when(quizService.generateQuiz(anyString())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/lessons/" + lessonId + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to generate quiz. Please try again."));
    }

    // Verify returned quiz questions have correct structure (question, options, correctAnswer)
    @Test
    public void testQuizQuestionStructure() throws Exception {
        List<QuizQuestion> mockQuiz = createMockQuiz();
        when(quizService.generateQuiz(anyString())).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/lessons/" + lessonId + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("What is Java?"))
                .andExpect(jsonPath("$[0].options[0]").value("A programming language"))
                .andExpect(jsonPath("$[0].options[1]").value("A coffee brand"))
                .andExpect(jsonPath("$[0].options[2]").value("An island"))
                .andExpect(jsonPath("$[0].options[3]").value("A database"))
                .andExpect(jsonPath("$[0].correctAnswer").value(0));
    }

    // Verify quiz contains expected number of questions
    @Test
    public void testQuizContainsExpectedNumberOfQuestions() throws Exception {
        List<QuizQuestion> mockQuiz = createMockQuiz();
        when(quizService.generateQuiz(anyString())).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/lessons/" + lessonId + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // Test with different lesson content lengths (short vs long)
    @Test
    public void testGenerateQuizWithShortContent() throws Exception {
        // Create lesson with short content
        Lesson shortLesson = new Lesson(
                "course123",
                "Short Lesson",
                "Java is a programming language.",
                "https://example.com/video",
                4);
        shortLesson = lessonRepository.save(shortLesson);

        List<QuizQuestion> mockQuiz = Arrays.asList(
                createQuizQuestion("What is Java?", 
                    Arrays.asList("A language", "A drink", "A place", "A tool"), 0)
        );
        when(quizService.generateQuiz(anyString())).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/lessons/" + shortLesson.getId() + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGenerateQuizWithLongContent() throws Exception {
        // Create lesson with long content
        String longContent = "Java is a high-level, class-based, object-oriented programming language. " +
                "It was originally developed by James Gosling at Sun Microsystems. " +
                "Java applications are typically compiled to bytecode that can run on any Java virtual machine. " +
                "The syntax of Java is similar to C and C++, but has fewer low-level facilities. " +
                "Java is platform-independent, meaning code can run on any platform with a JVM.";
        
        Lesson longLesson = new Lesson(
                "course123",
                "Long Lesson",
                longContent,
                "https://example.com/video",
                5);
        longLesson = lessonRepository.save(longLesson);

        List<QuizQuestion> mockQuiz = createMockQuiz();
        when(quizService.generateQuiz(anyString())).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/lessons/" + longLesson.getId() + "/quiz")
                .with(jwt().jwt(j -> j
                        .subject("student123")
                        .claim("email", "student@example.com")
                        .claim("user_roles", List.of("student")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // Helper method to create mock quiz questions
    private List<QuizQuestion> createMockQuiz() {
        return Arrays.asList(
                createQuizQuestion(
                        "What is Java?",
                        Arrays.asList("A programming language", "A coffee brand", "An island", "A database"),
                        0),
                createQuizQuestion(
                        "Is Java object-oriented?",
                        Arrays.asList("Yes", "No", "Sometimes", "Only in certain versions"),
                        0),
                createQuizQuestion(
                        "What are Java applications compiled to?",
                        Arrays.asList("Machine code", "Assembly", "Bytecode", "Binary"),
                        2)
        );
    }

    private QuizQuestion createQuizQuestion(String question, List<String> options, int correctAnswer) {
        QuizQuestion q = new QuizQuestion();
        q.setQuestion(question);
        q.setOptions(options);
        q.setCorrectAnswer(correctAnswer);
        return q;
    }
}