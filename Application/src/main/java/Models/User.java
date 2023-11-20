package Models;

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
}
