package Models;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static Constant.Constant.*;

public class StudyRecord {
    private int totalScore;
    private Ranking ranking;
    private int timesAttempt;
    private int totalQuestion;
    private int correctQuestions;
    private int incorrectQuestions;
    private Duration totalTimeSpend;

    //Hasmap lưu lại giời gian học bài mỗi ngày
    private HashMap<String, Duration> mapStudyTime = new HashMap<>();

    public StudyRecord(int totalScore, int timesAttempt, int totalQuestion
            , int correctQuestions, int incorrectQuestions, Duration totalTimeSpend,
                       HashMap<String, Duration> mapStudyTime) {
        this.totalScore = totalScore;
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
        if (this.totalScore < 1000) {
            return Ranking.BRONZE;
        } else if (this.totalScore < 3000) {
            return Ranking.SILVER;
        } else if (this.totalScore < 6000) {
            return Ranking.GOLD;
        } else if (this.totalScore < 10000) {
            return Ranking.PLATINUM;
        } else if (this.totalScore < 15000) {
            return Ranking.DIAMOND;
        } else if (this.totalScore < 20000) {
            return Ranking.MASTER;
        } else if (this.totalScore < 25000) {
            return Ranking.GRANDMASTER;
        } else {
            return Ranking.CHALLENGER;
        }
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

    public Duration getTotalTimeSpend() {
        return totalTimeSpend;
    }

    public void setTotalTimeSpend(Duration totalTimeSpend) {
        this.totalTimeSpend = totalTimeSpend;
    }

    public HashMap<String, Duration> getMapStudyTime() {
        return mapStudyTime;
    }

    public void setMapStudyTime(HashMap<String, Duration> mapStudyTime) {
        this.mapStudyTime = mapStudyTime;
    }

    // Chuyển đối tượng thành chuỗi JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Chuyển chuỗi JSON thành đối tượng
    public static StudyRecord fromJson(String jsonString) {
        Gson gson = new Gson();
        StudyRecord studyRecord = null;

        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(jsonString));
            jsonReader.setLenient(true); // Tùy chọn, nếu muốn chấp nhận JSON không đúng cú pháp

            studyRecord = gson.fromJson(jsonReader, StudyRecord.class);
        } catch (JsonSyntaxException e) {
            // Xử lý lỗi định dạng JSON
            e.printStackTrace();
            // Hoặc ném một ngoại lệ hoặc trả về giá trị mặc định tùy thuộc vào yêu cầu
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác nếu cần
            e.printStackTrace();
        }

        return studyRecord;
    }

    public static StudyRecord readRecordFile() {
        File file = new File(STUDY_RECORD_FILE);
        if (file.exists() && file.isFile()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(STUDY_RECORD_FILE);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            try {
                StringBuilder contentBuilder = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    contentBuilder.append((char) c);
                }
                String content = contentBuilder.toString();
                return fromJson(content);

            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
        }
        return null;
    }

    public void writeStudyRecord() {
        String recordString = this.toJson();

        File file = new File(STUDY_RECORD_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {

                writer.write(recordString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
