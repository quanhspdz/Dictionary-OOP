package Controllers;

import Models.StudyRecord;
import Models.User;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static Constant.Constant.FIREBASE_KEY;
import static Constant.Key.firebaseDatabaseUrl;
import static Controllers.LearningOverviewController.listRankingUsers;

public class BaseController {
    @FXML
    AnchorPane container;

    protected static Timeline countdownTimer;

    protected static User user;

    public static HashMap<String, User> mapUsers = new HashMap<>();
    public static ArrayList<User> listUsers = new ArrayList<>();

    public void setNode(Node node, AnchorPane container) {
        container.getChildren().clear();
        container.getChildren().add(node);
    }

    public void showComponent(String path) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            AnchorPane component = loader.load();

            // Sử dụng thuộc tính ràng buộc (constraints) để thiết lập kích thước và vị trí của component
            AnchorPane.setTopAnchor(component, 0.0);
            AnchorPane.setRightAnchor(component, 0.0);
            AnchorPane.setBottomAnchor(component, 0.0);
            AnchorPane.setLeftAnchor(component, 0.0);

            setNode(component, container);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        BaseController.user = user;
    }

    public void initFirebase() {
        try {
            // Load Firebase Admin SDK JSON file
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(FIREBASE_KEY);

            if (serviceAccount == null) {
                throw new IOException("Failed to load Firebase Admin SDK JSON file");
            }

            // Initialize Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(firebaseDatabaseUrl)
                    .build();

            FirebaseApp.initializeApp(options);

            // Close the InputStream after initialization
            serviceAccount.close();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    getListUserData();
                }
            });
        } catch (IOException e) {
            // Handle initialization failure
            e.printStackTrace();
        }
    }

    public void getListUserData() {
        // Lắng nghe sự thay đổi trên node "users"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUsers.clear();
                mapUsers.clear();
                // Lặp qua tất cả các người dùng và thêm vào danh sách
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    User retrievedUser = getUser(childSnapshot);
                    listUsers.add(retrievedUser);
                    mapUsers.put(retrievedUser.getUsername(), retrievedUser);
                    System.out.println("+1 user: " + retrievedUser.getUsername());
                }
                if (listRankingUsers != null) {
                    listRankingUsers.clear();
                    sortRankingByPoint();
                    listRankingUsers.addAll(listUsers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                System.err.println("Database read canceled: " + databaseError.getMessage());
            }
        });
    }

    public static User getUser(DataSnapshot childSnapshot) {
        Map<String, Object> userData = (Map<String, Object>) childSnapshot.getValue();
        Map<String, Object> mapRecord = (Map<String, Object>) userData.get("studyRecord");

        // Retrieve data for StudyRecord
        int totalScore = mapRecord.get("totalScore") != null ? ((Long) mapRecord.get("totalScore")).intValue() : 0;
        int timesAttempt = mapRecord.get("timesAttempt") != null ? ((Long) mapRecord.get("timesAttempt")).intValue() : 0;
        int totalQuestion = mapRecord.get("totalQuestion") != null ? ((Long) mapRecord.get("totalQuestion")).intValue() : 0;
        int correctQuestions = mapRecord.get("correctQuestions") != null ? ((Long) mapRecord.get("correctQuestions")).intValue() : 0;
        int incorrectQuestions = mapRecord.get("incorrectQuestions") != null ? ((Long) mapRecord.get("incorrectQuestions")).intValue() : 0;

        Duration totalTimeSpend = null;
        Object totalTimeSpendObject = mapRecord.get("totalTimeSpend");

        if (totalTimeSpendObject instanceof HashMap) {
            Map<String, Object> mapTotalTimeSpend = (Map<String, Object>) totalTimeSpendObject;

            Long seconds = (Long) mapTotalTimeSpend.get("seconds");
            Long nano = (Long) mapTotalTimeSpend.get("nano");

            Duration duration = Duration.ofSeconds(seconds, nano);

            // Sử dụng duration như là một Duration
            totalTimeSpend = duration;
        }


        Map<String, Duration> mapStudyTime = new HashMap<>();

        Object mapStudyTimeObject = mapRecord.get("mapStudyTime");

        if (mapStudyTimeObject instanceof HashMap) {
            HashMap<String, HashMap<String, Object>> mapTotalStudyTime
                    = (HashMap<String, HashMap<String, Object>>) mapStudyTimeObject;

            for (Map.Entry<String, HashMap<String, Object>> entry : mapTotalStudyTime.entrySet()) {
                String key = entry.getKey();
                HashMap<String, Object> innerMap = entry.getValue();

                // Assuming the inner HashMap contains "seconds" and "nano" fields
                Long seconds = (Long) innerMap.get("seconds");
                Long nano = (Long) innerMap.get("nano");

                Duration duration = Duration.ofSeconds(seconds, nano);

                // Add the duration to the map
                mapStudyTime.put(key, duration);
            }
        }

        // Create a StudyRecord object
        StudyRecord studyRecord = new StudyRecord(
                totalScore,
                timesAttempt,
                totalQuestion,
                correctQuestions,
                incorrectQuestions,
                totalTimeSpend,
                (HashMap<String, Duration>) mapStudyTime
        );

        // Create a User object
        User retrievedUser = new User(
                (String) userData.get("userId"),
                (String) userData.get("username"),
                (String) userData.get("email"),
                (String) userData.get("password"),
                studyRecord
        );

        return retrievedUser;
    }

    public void sortRankingByPoint() {
        Collections.sort(listUsers);
        Collections.reverse(listUsers);
    }
}
