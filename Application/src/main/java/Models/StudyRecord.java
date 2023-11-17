package Models;

import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class StudyRecord {
    private int totalScore;
    private Ranking ranking;
    private int timesAttempt;
    private int totalQuestion;
    private int correctQuestions;
    private int incorrectQuestions;
    private double totalTimeSpend;

    //Hasmap lưu lại giời gian học bài mỗi ngày
    private HashMap<Time, Time> mapStudyTime = new HashMap<>();

    public StudyRecord(int totalScore, Ranking ranking, int timesAttempt, int totalQuestion
            , int correctQuestions, int incorrectQuestions, double totalTimeSpend,
                       HashMap<Time, Time> mapStudyTime) {
        this.totalScore = totalScore;
        this.ranking = ranking;
        this.timesAttempt = timesAttempt;
        this.totalQuestion = totalQuestion;
        this.correctQuestions = correctQuestions;
        this.incorrectQuestions = incorrectQuestions;
        this.totalTimeSpend = totalTimeSpend;
        this.mapStudyTime = mapStudyTime;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    public int getTimesAttempt() {
        return timesAttempt;
    }

    public void setTimesAttempt(int timesAttempt) {
        this.timesAttempt = timesAttempt;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public int getCorrectQuestions() {
        return correctQuestions;
    }

    public void setCorrectQuestions(int correctQuestions) {
        this.correctQuestions = correctQuestions;
    }

    public int getIncorrectQuestions() {
        return incorrectQuestions;
    }

    public void setIncorrectQuestions(int incorrectQuestions) {
        this.incorrectQuestions = incorrectQuestions;
    }

    public double getTotalTimeSpend() {
        return totalTimeSpend;
    }

    public void setTotalTimeSpend(double totalTimeSpend) {
        this.totalTimeSpend = totalTimeSpend;
    }

    public HashMap<Time, Time> getMapStudyTime() {
        return mapStudyTime;
    }

    public void setMapStudyTime(HashMap<Time, Time> mapStudyTime) {
        this.mapStudyTime = mapStudyTime;
    }

    // Chuyển đối tượng thành chuỗi JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Chuyển chuỗi JSON thành đối tượng
    public static Question fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Question.class);
    }
}
