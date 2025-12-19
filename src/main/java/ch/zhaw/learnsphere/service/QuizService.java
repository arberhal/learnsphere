package ch.zhaw.learnsphere.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.zhaw.learnsphere.model.QuizQuestion;

@Service
public class QuizService {

    @Autowired
    private ChatClient chatClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate 5 multiple choice questions from lesson content
     */
    public List<QuizQuestion> generateQuiz(String lessonContent) {
        try {
            String promptText = """
                Generate 5 multiple choice questions based on this lesson content:
                
                %s
                
                Return ONLY a JSON array with this exact format, no other text:
                [
                  {
                    "question": "Question text here?",
                    "options": ["Option A", "Option B", "Option C", "Option D"],
                    "correctAnswer": 0
                  }
                ]
                
                Rules:
                - correctAnswer is the index (0-3) of the correct option
                - Make questions challenging but fair
                - Options should be plausible but only one correct
                """.formatted(lessonContent);

            // Use ChatClient to call AI
            String jsonResponse = chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content();
            
            // Remove markdown code blocks if present
            jsonResponse = jsonResponse.replaceAll("```json\\n?", "")
                                       .replaceAll("```\\n?", "")
                                       .trim();
            
            // Parse JSON response
            JsonNode root = objectMapper.readTree(jsonResponse);
            List<QuizQuestion> questions = new ArrayList<>();
            
            for (JsonNode node : root) {
                QuizQuestion question = new QuizQuestion();
                question.setQuestion(node.get("question").asText());
                
                List<String> options = new ArrayList<>();
                for (JsonNode option : node.get("options")) {
                    options.add(option.asText());
                }
                question.setOptions(options);
                question.setCorrectAnswer(node.get("correctAnswer").asInt());
                
                questions.add(question);
            }
            
            return questions;
            
        } catch (Exception e) {
            System.err.println("Quiz generation failed: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}