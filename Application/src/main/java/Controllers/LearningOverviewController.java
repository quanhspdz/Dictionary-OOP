package Controllers;

import Models.StudyRecord;
import Models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static Constant.Constant.*;

public class LearningOverviewController extends BaseController implements Initializable {

    @FXML
    ImageView imageRanking;
    @FXML
    Label labelRanking, labelTotalAttempt, labelTimeSpend,
            labelCorrectRatio, labelTimeAverage, progressTitle;
    @FXML
    ListView<User> listViewRanking;
    @FXML
    PieChart pieChartQuestion;
    @FXML
    LineChart<String, Number> lineChartTime;
    @FXML
    Button btnOffline, btnPK;

    public static ObservableList<User> listRankingUsers;

    public static StudyRecord studyRecord;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getListUserData();

        btnOffline.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/LearningEngView.fxml");
            }
        });

        loadStudyRecord();
    }

    private void loadStudyRecord() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                studyRecord = StudyRecord.readRecordFile();
                if (user != null) {
                    if (user.getStudyRecord() != null) {
                        studyRecord = user.getStudyRecord();
                    }
                }
                if (studyRecord != null) {
                    setupRanking(studyRecord);
                    setupPieChart(studyRecord);
                    setupLineChart(studyRecord);
                    setupRecordLabel(studyRecord);
                }
                setupRankingBoard();
            }
        });
    }

    private void setupRecordLabel(StudyRecord studyRecord) {
        progressTitle.setText("Thông số - " + user.getUsername());
        int totalQuestions = studyRecord.getTotalQuestion();

        // Đảm bảo chia số nguyên để tránh mất thông tin khi tính toán tỉ lệ
        double correctRatio = ((double) studyRecord.getCorrectQuestions() / totalQuestions) * 100;

        // Chia thời gian theo số lần thử để tính thời gian trung bình mỗi lần thử
        double timeAverage = (double) studyRecord.getTotalTimeSpend().toSeconds() / (studyRecord.getTimesAttempt() * 10);

        Duration totalTimeDuration = studyRecord.getTotalTimeSpend();
        long hours = totalTimeDuration.toHours();
        long minutes = totalTimeDuration.minusHours(hours).toMinutes();
        long seconds = totalTimeDuration.minusHours(hours).minusMinutes(minutes).getSeconds();

        String timeSpendText = "";
        if (hours > 0) {
            timeSpendText += hours + " giờ ";
        }
        if (minutes > 0 || (hours == 0 && seconds == 0)) {
            timeSpendText += minutes + " phút ";
        }
        if (seconds > 0 || (hours == 0 && minutes == 0)) {
            timeSpendText += seconds + " giây";
        }
        labelTimeSpend.setWrapText(true);
        labelTotalAttempt.setText("Tổng số lần học: " + studyRecord.getTimesAttempt());
        labelTimeSpend.setText("Tổng thời gian học: " + timeSpendText);
        labelCorrectRatio.setText("Tỉ lệ trả lời đúng: " + String.format("%.2f", correctRatio) + "%");
        labelTimeAverage.setText("Thời gian trung bình: " + String.format("%.2f", timeAverage) + "s/q");
    }

    private void setupRankingBoard() {
        sortRankingByPoint();
        listRankingUsers = FXCollections.observableArrayList(listUsers);

        // Add a listener to update the ListView when the studyRecords list changes
        listRankingUsers.addListener((ListChangeListener<User>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    // Update the ListView
                    updateListView(listRankingUsers);
                    System.out.println("SOMETHING CHANGE!");
                }
            }
        });
        updateListView(listRankingUsers);
    }

    private void updateListView(ObservableList<User> listRankingUsers) {
        listViewRanking.setItems(listRankingUsers);
        // Tùy chỉnh cách hiển thị mỗi ô trong ListView
        listViewRanking.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Đặt lại cỡ chữ mặc định nếu mục trống hoặc null
                } else {
                    StudyRecord studyRecord = item.getStudyRecord();
                    String point = "NA";
                    if (studyRecord != null) {
                        point = studyRecord.getTotalScore() + "";
                    }
                    int index = getIndex() + 1;
                    setText(index + ".\t" + item.getUsername() + " " + point + " điểm");

                    String style = "-fx-font-size: 13px;";
                    if (user.getUsername().equals(item.getUsername())) {
                        // Hiển thị màu xanh lá cho tên current user
                        style += "-fx-text-fill: #12bd69;";
                    }
                    setStyle(style);
                }
            }
        });
    }


    private void setupLineChart(StudyRecord studyRecord) {
        HashMap<String, Duration> mapStudyDay = studyRecord.getMapStudyTime();

        // Create series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Thời gian học mỗi ngày");

        // Extract and sort dates
        List<String> sortedDates = new ArrayList<>(mapStudyDay.keySet());
        Collections.reverse(sortedDates);

        // Add sorted data to the series
        for (String date : sortedDates) {
            Duration duration = mapStudyDay.get(date);
            series.getData().add(new XYChart.Data<>(date, duration.toMinutes()));
        }

        // Add series to the LineChart
        lineChartTime.getData().add(series);
    }

    private void setupPieChart(StudyRecord studyRecord) {
        int totalQuestions = studyRecord.getTotalQuestion();
        int numberCorrect = studyRecord.getCorrectQuestions();
        int numberIncorrect = studyRecord.getIncorrectQuestions();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Đúng: " + numberCorrect, numberCorrect),
                new PieChart.Data("Sai: " + numberIncorrect, numberIncorrect)
        );

        // Create PieChart and add data
        pieChartQuestion.getData().addAll(pieChartData);
        // Set specific colors for each segment
        String correctStyle = "-fx-pie-color: #00FF00;";
        String incorrectStyle = "-fx-pie-color: #FFFF00;";
        pieChartData.get(0).getNode().setStyle(correctStyle);  // Green for "Correct Answers"
        pieChartData.get(1).getNode().setStyle(incorrectStyle);  // Yellow for "Incorrect Answers"
        pieChartData.get(0).getChart().setStyle(correctStyle);
        pieChartData.get(1).getChart().setStyle(incorrectStyle);
    }

    private void setupRanking(StudyRecord studyRecord) {
        switch (studyRecord.getRanking()) {
            case BRONZE -> {
                Image image = new Image(IMAGE_BRONZE);
                imageRanking.setImage(image);
            }
            case SILVER -> {
                Image image = new Image(IMAGE_SILVER);
                imageRanking.setImage(image);
            }
            case GOLD -> {
                Image image = new Image(IMAGE_GOLD);
                imageRanking.setImage(image);
            }
            case PLATINUM -> {
                Image image = new Image(IMAGE_PLATINUM);
                imageRanking.setImage(image);
            }
            case DIAMOND -> {
                Image image = new Image(IMAGE_DIAMOND);
                imageRanking.setImage(image);
            }
            case MASTER -> {
                Image image = new Image(IMAGE_MASTER);
                imageRanking.setImage(image);
            }
            case GRANDMASTER -> {
                Image image = new Image(IMAGE_GRANDMASTER);
                imageRanking.setImage(image);
            }
            case CHALLENGER -> {
                Image image = new Image(IMAGE_CHALLENGER);
                imageRanking.setImage(image);
            }
        }

        labelRanking.setText(studyRecord.getRanking().getValue() + " "
                + studyRecord.getTotalScore() + " points");
    }
}
