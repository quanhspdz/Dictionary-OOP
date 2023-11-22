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

    private boolean isSavePassword = false;

    public User(String userId, String username, String email, String password, StudyRecord studyRecord) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.studyRecord = studyRecord;
    }

    public User(String userId, String username, String email, String password, StudyRecord studyRecord, boolean isSavePassword) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.studyRecord = studyRecord;
        this.isSavePassword = isSavePassword;
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

    public boolean isSavePassword() {
        return isSavePassword;
    }

    public void setSavePassword(boolean savePassword) {
        isSavePassword = savePassword;
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

    public static User readUserFromFile() {
        File file = new File(USER_INFO_FILE);
        if (file.exists() && file.isFile()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(USER_INFO_FILE);
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

    public static User fromJson(String jsonString) {
        Gson gson = new Gson();
        User user = null;

        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(jsonString));
            jsonReader.setLenient(true); // Tùy chọn, nếu muốn chấp nhận JSON không đúng cú pháp

            user = gson.fromJson(jsonReader, User.class);
        } catch (JsonSyntaxException e) {
            // Xử lý lỗi định dạng JSON
            e.printStackTrace();
            // Hoặc ném một ngoại lệ hoặc trả về giá trị mặc định tùy thuộc vào yêu cầu
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác nếu cần
            e.printStackTrace();
        }

        return user;
    }
}
