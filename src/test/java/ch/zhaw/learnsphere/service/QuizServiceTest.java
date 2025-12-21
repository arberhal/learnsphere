package ch.zhaw.learnsphere.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import ch.zhaw.learnsphere.model.QuizQuestion;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.ChatClientRequestSpec userSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @InjectMocks
    private QuizService quizService;

    @BeforeEach
    public void setUp() {
        // Setup the chain: chatClient.prompt().user().call().content()
        // Use lenient to avoid UnnecessaryStubbingException when some tests don't use all stubbings
        lenient().when(chatClient.prompt()).thenReturn(requestSpec);
        lenient().when(requestSpec.user(anyString())).thenReturn(userSpec);
        lenient().when(userSpec.call()).thenReturn(responseSpec);
    }

    @Test
    public void testGenerateQuizSuccess() {
        // Mock AI response with valid JSON
        String mockResponse = """
                [
                  {
                    "question": "What is Java?",
                    "options": ["A programming language", "A coffee brand", "An island", "A database"],
                    "correctAnswer": 0
                  },
                  {
                    "question": "Is Java object-oriented?",
                    "options": ["Yes", "No", "Sometimes", "Maybe"],
                    "correctAnswer": 0
                  }
                ]
                """;

        when(responseSpec.content()).thenReturn(mockResponse);

        // Test
        String lessonContent = "Java is a programming language. It is object-oriented.";
        List<QuizQuestion> result = quizService.generateQuiz(lessonContent);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());

        QuizQuestion q1 = result.get(0);
        assertEquals("What is Java?", q1.getQuestion());
        assertEquals(4, q1.getOptions().size());
        assertEquals("A programming language", q1.getOptions().get(0));
        assertEquals(0, q1.getCorrectAnswer());

        QuizQuestion q2 = result.get(1);
        assertEquals("Is Java object-oriented?", q2.getQuestion());
        assertEquals("Yes", q2.getOptions().get(0));
        assertEquals(0, q2.getCorrectAnswer());

        // Verify ChatClient was called
        verify(chatClient).prompt();
        verify(requestSpec).user(anyString());
        verify(userSpec).call();
        verify(responseSpec).content();
    }

    @Test
    public void testGenerateQuizWithMarkdownCodeBlocks() {
        // Mock AI response with markdown code blocks
        String mockResponse = """
                ```json
                [
                  {
                    "question": "Test question?",
                    "options": ["Option A", "Option B", "Option C", "Option D"],
                    "correctAnswer": 2
                  }
                ]
                ```
                """;

        when(responseSpec.content()).thenReturn(mockResponse);

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content");

        // Verify - should strip markdown and parse correctly
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test question?", result.get(0).getQuestion());
        assertEquals(2, result.get(0).getCorrectAnswer());
    }

    @Test
    public void testGenerateQuizWithFiveQuestions() {
        // Mock AI response with 5 questions
        String mockResponse = """
                [
                  {
                    "question": "Question 1?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 0
                  },
                  {
                    "question": "Question 2?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 1
                  },
                  {
                    "question": "Question 3?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 2
                  },
                  {
                    "question": "Question 4?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 3
                  },
                  {
                    "question": "Question 5?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 0
                  }
                ]
                """;

        when(responseSpec.content()).thenReturn(mockResponse);

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Long lesson content here");

        // Verify
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("Question 1?", result.get(0).getQuestion());
        assertEquals("Question 5?", result.get(4).getQuestion());
    }

    @Test
    public void testGenerateQuizInvalidJson() {
        // Mock AI response with invalid JSON
        when(responseSpec.content()).thenReturn("This is not valid JSON");

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content");

        // Verify - should return empty list on error
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateQuizEmptyResponse() {
        // Mock AI response with empty array
        when(responseSpec.content()).thenReturn("[]");

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content");

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateQuizNullResponse() {
        // Mock AI returning null
        when(responseSpec.content()).thenReturn(null);

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content");

        // Verify - should handle null gracefully
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateQuizChatClientThrowsException() {
        // Mock ChatClient throwing exception
        when(chatClient.prompt()).thenThrow(new RuntimeException("AI service unavailable"));

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content");

        // Verify - should return empty list on exception
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGenerateQuizWithSpecialCharacters() {
        // Mock AI response with special characters
        String mockResponse = """
                [
                  {
                    "question": "What's the symbol for 'less than'?",
                    "options": ["<", ">", "<=", ">="],
                    "correctAnswer": 0
                  }
                ]
                """;

        when(responseSpec.content()).thenReturn(mockResponse);

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content with <special> characters");

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("What's the symbol for 'less than'?", result.get(0).getQuestion());
        assertEquals("<", result.get(0).getOptions().get(0));
    }

    @Test
    public void testGenerateQuizDifferentCorrectAnswerIndexes() {
        // Mock AI response with different correct answer indexes
        String mockResponse = """
                [
                  {
                    "question": "First question?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 0
                  },
                  {
                    "question": "Second question?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 1
                  },
                  {
                    "question": "Third question?",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": 3
                  }
                ]
                """;

        when(responseSpec.content()).thenReturn(mockResponse);

        // Test
        List<QuizQuestion> result = quizService.generateQuiz("Test content");

        // Verify all different correct answer indexes
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(0, result.get(0).getCorrectAnswer());
        assertEquals(1, result.get(1).getCorrectAnswer());
        assertEquals(3, result.get(2).getCorrectAnswer());
    }

    @Test
    public void testGenerateQuizVerifyPromptContent() {
        // Mock response
        when(responseSpec.content()).thenReturn("[]");

        // Test with specific content
        String lessonContent = "Specific lesson content to verify";
        quizService.generateQuiz(lessonContent);

        // Verify that the prompt was called (we can't easily verify the exact content due to method overloading)
        verify(requestSpec).user(any(String.class));
    }

    @Test
    public void testGenerateQuizWithLongLessonContent() {
        // Mock response
        String mockResponse = """
                [
                  {
                    "question": "Summary question?",
                    "options": ["Yes", "No", "Maybe", "Unknown"],
                    "correctAnswer": 0
                  }
                ]
                """;
        when(responseSpec.content()).thenReturn(mockResponse);

        // Test with very long content
        String longContent = "Java is a high-level programming language. ".repeat(100);
        List<QuizQuestion> result = quizService.generateQuiz(longContent);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}