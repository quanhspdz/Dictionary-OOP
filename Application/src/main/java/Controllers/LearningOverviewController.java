package Controllers;

import Models.StudyRecord;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.w3c.dom.Node;

import java.net.URL;
import java.sql.Time;
import java.time.Duration;
import java.util.*;

import static Constant.Constant.*;

public class LearningOverviewController extends BaseController implements Initializable {

    @FXML
    ImageView imageRanking;
    @FXML
    Label labelRanking;
    @FXML
    ListView<String> listViewRanking;
    @FXML
    PieChart pieChartQuestion;
    @FXML
    LineChart<String, Number> lineChartTime;
    @FXML
    Button btnOffline, btnPK;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOffline.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/LearningEngView.fxml");
            }
        });

        loadStudyRecord();
    }

    private void loadStudyRecord() {
        StudyRecord studyRecord = StudyRecord.readRecordFile();
        if (studyRecord != null) {
            setupRanking(studyRecord);
            setupPieChart(studyRecord);
            setupLineChart(studyRecord);
            setupRankingBoard();
        }
    }

    private void setupRankingBoard() {

    }

    private void setupLineChart(StudyRecord studyRecord) {
        HashMap<String, Duration> mapStudyDay = studyRecord.getMapStudyTime();

        // Create series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Thời gian học mỗi ngày");

        // Reverse the order of entries in the HashMap
        List<Map.Entry<String, Duration>> entryList = new ArrayList<>(mapStudyDay.entrySet());
        Collections.reverse(entryList);

        // Add reversed data to the series
        // Add reversed data to the series
        for (Map.Entry<String, Duration> entry : entryList) {
            String date = entry.getKey();
            Duration duration = entry.getValue();

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
