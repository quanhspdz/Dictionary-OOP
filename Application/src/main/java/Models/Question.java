package Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static Constant.Constant.DATA_FILE_PATH;
import static Constant.Constant.QUESTION_FILE_PATH;

public class Question {
    private String questionTitle;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctAnswer;
    private int difficulty; //from 1 to 3
    private String subject;

    public Question(String questionTitle, String answerA, String answerB,
                    String answerC, String answerD, String correctAnswer,
                    int difficulty, String subject) {
        this.questionTitle = questionTitle;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
        this.subject = subject;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Chuyển đối tượng Question thành chuỗi JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Chuyển chuỗi JSON thành đối tượng Question
    public static Question fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Question.class);
    }

    public static void readQuestionFile(ArrayList<Question> questions) {
        ClassLoader classLoader = Question.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(QUESTION_FILE_PATH);

        if (inputStream == null) {
            System.err.println("File not found: " + QUESTION_FILE_PATH);
            return;
        }

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        try {
            StringBuilder contentBuilder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                contentBuilder.append((char) c);
            }

            String content = contentBuilder.toString();
            JSONParser parser = new JSONParser();
            JSONArray questionsArray = (JSONArray) parser.parse(content);
            for (Object o : questionsArray) {
                Question question = getQuestionFromJson((JSONObject) o);

                questions.add(question);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Error closing reader: " + e.getMessage());
            }
        }
    }

    private static Question getQuestionFromJson(JSONObject o) {
        JSONObject questionObject = o;

        String questionTitle = (String) questionObject.get("questionTitle");
        String answerA = (String) questionObject.get("answerA");
        String answerB = (String) questionObject.get("answerB");
        String answerC = (String) questionObject.get("answerC");
        String answerD = (String) questionObject.get("answerD");
        String correctAnswer = (String) questionObject.get("correctAnswer");
        long difficulty = (long) questionObject.get("difficulty"); // Note: casting to long
        String subject = (String) questionObject.get("subject");

        Question question = new Question(questionTitle, answerA, answerB, answerC, answerD,
                correctAnswer, (int) difficulty, subject);
        return question;
    }

    @Override
    public String toString() {
        return "Question: " + questionTitle +
                "\nA. " + answerA +
                "\nB. " + answerB +
                "\nC. " + answerC +
                "\nD. " + answerD +
                "\nCorrect Answer: " + correctAnswer +
                "\nDifficulty: " + difficulty +
                "\nSubject: " + subject;
    }
}
