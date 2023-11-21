package Models;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static Constant.Constant.STUDY_RECORD_FILE;
import static Constant.Constant.USER_INFO_FILE;

public class User implements Comparable<User> {
    private String userId;
    private String username;
    private String email;
    private String password;
    private StudyRecord studyRecord;

    public User(String userId, String username, String email, String password, StudyRecord studyRecord) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.studyRecord = studyRecord;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StudyRecord getStudyRecord() {
        return studyRecord;
    }

    public void setStudyRecord(StudyRecord studyRecord) {
        this.studyRecord = studyRecord;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", studyRecord=" + studyRecord +
                '}';
    }

    @Override
    public int compareTo(User other) {
        StudyRecord thisStudyRecord = this.studyRecord;
        StudyRecord otherStudyRecord = other.studyRecord;

        // Xử lý trường hợp studyRecord có thể là null
        if (thisStudyRecord == null && otherStudyRecord == null) {
            return 0; // Cả hai là null, xem như bằng nhau
        } else if (thisStudyRecord == null) {
            return -1; // this là null, nhỏ hơn other (other không null)
        } else if (otherStudyRecord == null) {
            return 1; // other là null, lớn hơn this (this không null)
        }

        // Tiếp tục so sánh nếu cả hai không null
        int score = thisStudyRecord.getTotalScore();
        int otherScore = otherStudyRecord.getTotalScore();
        return Integer.compare(score, otherScore);
    }

    public void writeUserInfo() {
        String userInfo = this.toJson();

        File file = new File(USER_INFO_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {

                writer.write(userInfo);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Chuyển đối tượng thành chuỗi JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
