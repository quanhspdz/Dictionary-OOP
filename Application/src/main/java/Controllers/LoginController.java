package Controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.util.*;

import DialogAlert.AlertMessage;
import Models.StudyRecord;
import Models.User;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static Constant.Constant.FIREBASE_KEY;
import static Constant.Key.firebaseDatabaseUrl;


public class LoginController extends BaseController implements Initializable {

    @FXML
    private Button changePass_backBtn, changePass_proceedBtn, forgot_backBtn, forgot_proceedBtn,
            login_btn, login_create, signup_btn, signup_loginAccount;

    @FXML
    private TextField changePass_cPassword, changePass_password, forgot_answer,
            forgot_username, login_username, signup_answer,
            signup_email, signup_username, login_showPassword;

    @FXML
    private PasswordField login_password, signup_password, signup_cPassword;

    @FXML
    private AnchorPane changePass_form, forgot_form, login_form, main_form, signup_form;

    @FXML
    private ComboBox<?> forgot_selectQuestion, signup_selectQuestion;

    @FXML
    private CheckBox login_selectShowPassword;

    @FXML
    private Hyperlink login_forgotPassword;


    private Connection connect;

    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;
    private AlertMessage alert;

    public void login() {
        alert = new AlertMessage();
        if (login_username.getText().isEmpty() || login_password.getText().isEmpty()) {
            alert.errorMessage("Thông tin đăng nhập không chính xác, vui lòng kiểm tra lại");
        } else {
            String username = login_username.getText();
            if (mapUsers.containsKey(username)) {
                String userPassword = mapUsers.get(username).getPassword();
                String typedPassword = login_password.getText().trim();
                Platform.runLater(() -> {
                    if (typedPassword.equals(userPassword)) {
                        //alert.successMessage("Đăng nhập thành công!");
                        try {
                            user = mapUsers.get(username);
                            goToMainApp();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        alert.errorMessage("Thông tin đăng nhập không chính xác, vui lòng kiểm tra lại");
                    }
                });
            } else {
                alert.errorMessage("Thông tin đăng nhập không chính xác, vui lòng kiểm tra lại");
            }
        }
    }

    private void goToMainApp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/DictionaryView.fxml"));
        Parent root = loader.load();

        // Access the controller of the new scene if needed
        // Example: MainController mainController = loader.getController();

        Scene scene = new Scene(root);
        Stage mainStage = new Stage();
        mainStage.setScene(scene);
        mainStage.show();

        // Close the current login window
        Stage currentStage = (Stage) login_btn.getScene().getWindow();
        currentStage.close();
    }

    public void showPassword() {

        if (login_selectShowPassword.isSelected()) {
            login_showPassword.setText(login_password.getText());
            login_showPassword.setVisible(true);
            login_password.setVisible(false);
        } else {
            login_password.setText(login_showPassword.getText());
            login_showPassword.setVisible(false);
            login_password.setVisible(true);
        }

    }

    public void forgotPassword() {

        AlertMessage alert = new AlertMessage();

        if (forgot_username.getText().isEmpty()
                || forgot_selectQuestion.getSelectionModel().getSelectedItem() == null
                || forgot_answer.getText().isEmpty()) {
            alert.errorMessage("Vui lòng nhập đầy đủ thông tin");
        } else {

            String checkData = "SELECT username, question, answer FROM users "
                    + "WHERE username = ? AND question = ? AND answer = ?";

            try {

                prepare = connect.prepareStatement(checkData);
                prepare.setString(1, forgot_username.getText());
                prepare.setString(2, (String) forgot_selectQuestion.getSelectionModel().getSelectedItem());
                prepare.setString(3, forgot_answer.getText());

                result = prepare.executeQuery();
                // IF CORRECT
                if (result.next()) {
                    // PROCEED TO CHANGE PASSWORD
                    signup_form.setVisible(false);
                    login_form.setVisible(false);
                    forgot_form.setVisible(false);
                    changePass_form.setVisible(true);
                } else {
                    alert.errorMessage("Thông tin không chính xác");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void forgotListQuestion() {

        List<String> listQ = new ArrayList<>();

        for (String data : questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        forgot_selectQuestion.setItems(listData);

    }

    public void register() {
        AlertMessage alert = new AlertMessage();

        if (signup_email.getText().isEmpty() || signup_username.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()
                || signup_selectQuestion.getSelectionModel().getSelectedItem() == null
                || signup_answer.getText().isEmpty()) {
            alert.errorMessage("Bạn cần nhập đầy đủ thông tin");
        } else if (!signup_password.getText().equals(signup_cPassword.getText())) {
            alert.errorMessage("Mật khẩu xác nhận không trùng khớp");
        } else if (signup_password.getText().length() < 8) {
            alert.errorMessage("Mật khẩu phải có ít nhất 8 ký tự");
        } else {
            // Check if the username & email already exists in Firebase
            String username = signup_username.getText();
            String email = signup_email.getText();

            if (mapUsers.containsKey(username)) {
                alert.errorMessage(username + " đã tồn tại");
            } else {
                // Create a new User object
                User newUser = new User(
                        UUID.randomUUID().toString(),  // Generate a unique user ID
                        signup_username.getText(),
                        signup_email.getText(),
                        signup_password.getText(),
                        new StudyRecord() 
                );

                // Save the user to Firebase Realtime Database
                saveUserToFirebase(newUser);
                mapUsers.put(newUser.getUsername(), newUser);
                listUsers.add(newUser);

                alert.successMessage("Đăng ký thành công!");
                try {
                    user = newUser;
                    goToMainApp();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // TO CLEAR ALL FIELDS OF REGISTRATION FORM
    public void registerClearFields() {
        signup_email.setText("");
        signup_username.setText("");
        signup_password.setText("");
        signup_cPassword.setText("");
        signup_selectQuestion.getSelectionModel().clearSelection();
        signup_answer.setText("");
    }

    public void changePassword() {

    }

    public void switchForm(ActionEvent event) {

        // the register form will be visible
        if (event.getSource() == signup_loginAccount || event.getSource() == forgot_backBtn) {
            signup_form.setVisible(false);
            login_form.setVisible(true);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_create) { // the login form will be visible
            signup_form.setVisible(true);
            login_form.setVisible(false);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_forgotPassword) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);
            // show data on combobox
            forgotListQuestion();
        } else if (event.getSource() == changePass_backBtn) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);
        }

    }

    private String[] questionList = {
            "Món ăn nào bạn thích nhất",
            "Màu sắc yêu thích nhất của bạn?",
            "Tên thú cưng của bạn?",
            "Môn thể thao bạn yêu thích nhất ?"};

    public void questions() {
        List<String> listQ = new ArrayList<>();

        for (String data : questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        signup_selectQuestion.setItems(listData);
    }

    public static void saveUserToFirebase(User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Create a map with user data
        // You can customize this map based on your data structure
        // Here, we are using a simple structure with user ID as the key
        // Adjust it according to your database schema
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUserId());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("password", user.getPassword());
        userData.put("studyRecord", user.getStudyRecord());

        // Save user data to Firebase Realtime Database
        databaseReference.child(user.getUserId()).setValueAsync(userData);
        System.out.println("User data saved to Firebase Realtime Database.");
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

    private void getListUserData() {
        // Lắng nghe sự thay đổi trên node "users"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lặp qua tất cả các người dùng và thêm vào danh sách
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    User retrievedUser = getUser(childSnapshot);
                    listUsers.add(retrievedUser);
                    mapUsers.put(retrievedUser.getUsername(), retrievedUser);
                    System.out.println("+1 user: " + retrievedUser.getUsername());
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        questions();
        forgotListQuestion();

        initFirebase();
    }
}


