package Controllers;

import Models.Question;
import Models.StudyRecord;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static Constant.Constant.*;

public class LearningEngController extends BaseController implements Initializable {

    @FXML
    public AnchorPane bodyContainer, headerContainer;
    @FXML
    public Pane questionAndAnswerPane;
    @FXML
    private RadioButton answerBtnA, answerBtnB, answerBtnC, answerBtnD;
    @FXML
    private Button nextBtn, shuffleBtn, teamworkBtn, fiftyPercentBtn;
    @FXML
    private Circle circle1, circle2, circle3, circle4, circle5, circle6,
            circle7, circle8, circle9, circle10;
    @FXML
    private Label progressText1, progressText2, progressText3, progressText4,
            progressText5, progressText6, progressText7, progressText8, progressText9, progressText10,
            questionText, questionTitle, scoreLabel, countdownLabel;
    @FXML
    private VBox vboxListRanking;

    private Media correctAudioMedia;
    private MediaPlayer correctAudioMediaPlayer;
    private final Paint currentQuestion = Color.rgb(152, 206, 243),
            correctQuestion = Color.rgb(166, 232, 58),
            incorrectQuestion = Color.rgb(255, 216, 0);

    private ArrayList<Question> listQuestionData = new ArrayList<>();
    private ArrayList<Question> listQuestion = new ArrayList<>();
    private int currentQuestionIndex;

    private String answer = "";

    private boolean isCompleted = false;

    private boolean isHandledFinishAnimation = false;

    private Timeline mainTimeLine = new Timeline();

    private int score = 0;

    private final int correctScore = 10;

    private final int incorrectScore = -5;

    private boolean isUsedFiftyPercentHelp = false;
    private boolean isUsedShuffleHelp = false;
    private boolean isUsedTeamworkHelp = false;
    private final int countDownTime = 30;
    private AtomicInteger secondsRemaining;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        answerBtnA.setOnAction(event -> {
            answer = "A";
            answerBtnA.setSelected(true);
            answerBtnB.setSelected(false);
            answerBtnC.setSelected(false);
            answerBtnD.setSelected(false);
        });

        answerBtnB.setOnAction(event -> {
            answer = "B";
            answerBtnA.setSelected(false);
            answerBtnB.setSelected(true);
            answerBtnC.setSelected(false);
            answerBtnD.setSelected(false);
        });

        answerBtnC.setOnAction(event -> {
            answer = "C";
            answerBtnA.setSelected(false);
            answerBtnB.setSelected(false);
            answerBtnC.setSelected(true);
            answerBtnD.setSelected(false);
        });

        answerBtnD.setOnAction(event -> {
            answer = "D";
            answerBtnA.setSelected(false);
            answerBtnB.setSelected(false);
            answerBtnC.setSelected(false);
            answerBtnD.setSelected(true);
        });

        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkAnswer();
            }
        });

        setQuestionProgress(1, currentQuestion);

        setupQuestion();
        vboxListRanking.setVisible(false);

        //Các quyền trợ giúp
        //50 50
        fiftyPercentBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isUsedFiftyPercentHelp) {
                    handleFiftyPercentHelp();
                }
            }
        });

        //Tổ tư vấn
        teamworkBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isUsedTeamworkHelp) {
                    handleTeamWorkHelp();
                }
            }
        });

        //Đổi câu hỏi khác
        shuffleBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isUsedShuffleHelp) {
                    handleShuffleHelp();
                }
            }
        });
    }

    private void handleShuffleHelp() {
        isUsedShuffleHelp = true;
        shuffleBtn.setDisable(true);

        Question currentQuestion = listQuestion.get(currentQuestionIndex);

        boolean foundReplaceQuestion = false;
        Random random = new Random();
        while (!foundReplaceQuestion) {
            int randomIndex = random.nextInt(listQuestionData.size());
            Question question = listQuestionData.get(randomIndex);
            if (!question.equals(currentQuestion) && !listQuestion.contains(question)) {
                foundReplaceQuestion = true;
                currentQuestion = question;
            }
        }

        listQuestion.set(currentQuestionIndex, currentQuestion);
        countdownTimer.stop();
        showQuestion(currentQuestion);
    }

    private void handleTeamWorkHelp() {
        isUsedTeamworkHelp = true;
        teamworkBtn.setDisable(true);

        Question question = listQuestion.get(currentQuestionIndex);
        String answer = question.getCorrectAnswer();

        // Creating a dialog
        Dialog<String> dialog = new Dialog<String>();
        // Setting the title
        dialog.setTitle("Teamwork Help");
        // Adding an OK button
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        // Creating a GridPane to hold the ProgressBar, labels, and percentages
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Set the width and height of the DialogPane
        dialog.getDialogPane().setPrefWidth(500);  // Adjust as needed
        dialog.getDialogPane().setPrefHeight(200);  // Adjust as needed

        // Adding 4 ProgressBar, labels, and percentages to the GridPane
        double totalProbability = 0.0;  // Tổng xác suất

        // Đáp án đúng
        int correctAnswerIndex = getAnswerIndex(answer);
        double correctAnswerProbability = 0.6 + 0.4 * new Random().nextDouble();
        totalProbability += correctAnswerProbability;
        ProgressBar correctAnswerProgressBar = new ProgressBar();
        correctAnswerProgressBar.setPrefWidth(300);
        correctAnswerProgressBar.setProgress(correctAnswerProbability);
        Label correctAnswerLabel = new Label("Đáp án " + getAnswerChoice(correctAnswerIndex));
        Label correctAnswerPercentageLabel = new Label(formatPercentage(correctAnswerProbability));
        gridPane.add(correctAnswerLabel, 0, correctAnswerIndex);
        gridPane.add(correctAnswerProgressBar, 1, correctAnswerIndex);
        gridPane.add(correctAnswerPercentageLabel, 2, correctAnswerIndex);

        // Tạo danh sách đáp án sai
        List<Integer> incorrectAnswerIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correctAnswerIndex) {
                incorrectAnswerIndices.add(i);
            }
        }

        // Đáp án sai
        for (int i : incorrectAnswerIndices) {
            double incorrectAnswerProbability = 0.01 + 0.39 * new Random().nextDouble();
            totalProbability += incorrectAnswerProbability;
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(300);
            progressBar.setProgress(incorrectAnswerProbability);

            Label label = new Label("Đáp án " + getAnswerChoice(i));
            Label percentageLabel = new Label(formatPercentage(incorrectAnswerProbability));

            gridPane.add(label, 0, i);
            gridPane.add(progressBar, 1, i);
            gridPane.add(percentageLabel, 2, i);
        }

        // Đảm bảo tổng xác suất không vượt quá 100%
        if (totalProbability > 1.0) {
            // Tính toán sự thay đổi cần thiết để tổng xác suất bằng 1.0
            double scale = 1.0 / totalProbability;
            for (int i = 0; i < 4; i++) {
                ProgressBar progressBar = (ProgressBar) gridPane.getChildren().get(i * 3 + 1);  // Lấy ProgressBar từ GridPane
                double probability = progressBar.getProgress() * scale;
                progressBar.setProgress(probability);

                // Cập nhật Label phần trăm
                Label percentageLabel = (Label) gridPane.getChildren().get(i * 3 + 2);
                percentageLabel.setText(formatPercentage(probability));
            }
        }

        // Setting the content of the dialog to the GridPane
        dialog.getDialogPane().setContent(gridPane);

        // Center the OK button
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setOnShown(event -> dialog.getDialogPane().lookupButton(ButtonType.OK).requestFocus());

        // Showing the dialog
        dialog.showAndWait();
    }


    // Format a percentage with one decimal place
    private String formatPercentage(double value) {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(value * 100) + "%";
    }

    private String getAnswerChoice(int index) {
        // Map index to corresponding answer choices A, B, C, D
        switch (index) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return "";  // Handle the default case or error
        }
    }

    private int getAnswerIndex(String answer) {
        // Map answer choices A, B, C, D to corresponding indices
        switch (answer) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            default:
                return -1;  // Handle the default case or error
        }
    }

    private void handleFiftyPercentHelp() {
        isUsedFiftyPercentHelp = true;
        fiftyPercentBtn.setDisable(true);

        Question question = listQuestion.get(currentQuestionIndex);
        String answer = question.getCorrectAnswer();

        List<String> options = new ArrayList<>();
        options.add("A");
        options.add("B");
        options.add("C");
        options.add("D");

        // Remove the selected option
        options.remove(answer);

        // Shuffle the remaining options
        Collections.shuffle(options);

        // Randomly select two options from the remaining list
        int randomIndex1 = new Random().nextInt(2);
        int randomIndex2 = 1 - randomIndex1;

        String randomAnswer1 = options.get(randomIndex1);
        String randomAnswer2 = options.get(randomIndex2);

        disableAnswer(randomAnswer1);
        disableAnswer(randomAnswer2);
    }

    private void disableAnswer(String answer) {
        switch (answer) {
            case "A": {
                answerBtnA.setDisable(true);
                break;
            }
            case "B": {
                answerBtnB.setDisable(true);
                break;
            }
            case "C": {
                answerBtnC.setDisable(true);
                break;
            }
            case "D": {
                answerBtnD.setDisable(true);
                break;
            }
        }
    }

    public void pickQuestions() {
        if (listQuestionData.size() <= 10) {
            // If the list of available questions is 10 or fewer, use all of them
            listQuestion.addAll(listQuestionData);
        } else {
            // If there are more than 10 questions, pick 10 random questions
            Random random = new Random();
            ArrayList<Integer> selectedIndices = new ArrayList<>();

            while (selectedIndices.size() < 10) {
                int randomIndex = random.nextInt(listQuestionData.size());

                // Ensure we don't pick the same question more than once
                if (!selectedIndices.contains(randomIndex)) {
                    selectedIndices.add(randomIndex);
                }
            }

            // Add the randomly selected questions to listQuestion
            for (int index : selectedIndices) {
                listQuestion.add(listQuestionData.get(index));
            }
        }
    }


    private void nextQuestion() {
        if (currentQuestionIndex + 1 < listQuestion.size()) {
            currentQuestionIndex++;
            Question question = listQuestion.get(currentQuestionIndex);
            showQuestion(question);
            questionTitle.setText("Câu hỏi " + (currentQuestionIndex + 1) + ":");
        } else if (currentQuestionIndex + 1 >= listQuestion.size()) {
            //Disable buttons
            answerBtnA.setDisable(true);
            answerBtnB.setDisable(true);
            answerBtnC.setDisable(true);
            answerBtnD.setDisable(true);
            nextBtn.setDisable(true);

            isCompleted = true;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showScoreSummaryDialog();
                    saveTestRecord();
                }
            });
        }
    }

    private void saveTestRecord() {
        int numberOfCorrectAns = 0;
        long totalSeconds = 0;

        for (Question question : listQuestion) {
            if (question.isAnsIsCorrect()) {
                numberOfCorrectAns++;
            }
            if (question.getFinishedTime() != null) {
                totalSeconds += question.getFinishedTime().getSeconds();
            }
        }

        Duration duration = Duration.ofSeconds(totalSeconds);

        long minutes = duration.toMinutes();
        long seconds = totalSeconds -
                TimeUnit.MINUTES.toSeconds(minutes);

        updateTestRecord(score, numberOfCorrectAns, duration);
    }

    private void updateTestRecord(int score, int numberOfCorrectAns, Duration duration) {
        StudyRecord studyRecord = StudyRecord.readRecordFile();
        // Lấy ngày hôm nay
        Date currentDate = new Date();
        // Định dạng ngày thành chuỗi
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(currentDate);

        if (studyRecord == null) {
            HashMap<String, Duration> mapStudyTime = new HashMap<>();
            mapStudyTime.put(formattedDate, duration);
            studyRecord = new StudyRecord(
                    score,
                    1,
                    listQuestion.size(),
                    numberOfCorrectAns,
                    listQuestion.size() - numberOfCorrectAns,
                    duration,
                    mapStudyTime
            );
        } else {
            int newScore = studyRecord.getTotalScore() + score;
            Duration newTotalTime = studyRecord.getTotalTimeSpend().plus(duration);
            int newTotalQuestion = studyRecord.getTotalQuestion() + listQuestion.size();
            int newTimeAttempt = studyRecord.getTimesAttempt() + 1;
            int newCorrectAns = studyRecord.getCorrectQuestions() + numberOfCorrectAns;
            int newIncorrectAns = studyRecord.getIncorrectQuestions()
                    + listQuestion.size() - numberOfCorrectAns;

            Duration newDayStudyTime = studyRecord.getMapStudyTime().get(formattedDate);

            if (newDayStudyTime == null) {
                studyRecord.getMapStudyTime().put(formattedDate, duration);
            } else {
                newDayStudyTime = newDayStudyTime.plus(duration); // Cập nhật newDayStudyTime
                studyRecord.getMapStudyTime().put(formattedDate, newDayStudyTime);
            }

            studyRecord.setTotalScore(newScore);
            studyRecord.setTotalTimeSpend(newTotalTime);
            studyRecord.setTotalQuestion(newTotalQuestion);
            studyRecord.setTimesAttempt(newTimeAttempt);
            studyRecord.setCorrectQuestions(newCorrectAns);
            studyRecord.setIncorrectQuestions(newIncorrectAns);
        }

        studyRecord.writeStudyRecord();
    }

    private void showScoreSummaryDialog() {
        // Creating a dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo điểm số");

        // Set the width and height of the DialogPane
        alert.getDialogPane().setPrefWidth(500);  // Adjust as needed
        alert.getDialogPane().setPrefHeight(200);  // Adjust as needed

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(PATH_TO_IMAGE_FIREWORK);
        Image originalImage = new Image(inputStream);

        // Create a scaled-down version of the image
        double scaleFactor = 1.0 / 10.0;
        Image image = new Image(inputStream);
        ImageView imageView = new ImageView(image);
        // Set the image view properties
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(alert.getDialogPane().getPrefWidth()); // Adjust fit width to match the dialog width
        imageView.setFitHeight(alert.getDialogPane().getPrefHeight() / 2); // Adjust fit height if needed

        alert.setHeaderText("Chúc mừng bạn đã hoàn thành bài thi!\n");

        String scoreStr = "";

        // Set the content text based on the score
        scoreStr += "Bạn đã đạt được: " + score + "/100 điểm!\n";

        if (score >= 80) {
            scoreStr += "Bạn thật xuất sắc! <3";
        } else if (score >= 60) {
            scoreStr += "Amazing good job nha bạn! :))))";
        } else {
            scoreStr += "Cố gắng hơn lần sau bạn nha! :((";
        }

        // Customize the dialog pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add additional content to the GridPane if needed
        Label additionalLabel = new Label(scoreStr);
        gridPane.add(additionalLabel, 0, 1); // Example of adding an additional label

        // Set the content of the dialog to the GridPane
        alert.getDialogPane().setContent(gridPane);

        // Show the dialog
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setOnShown(event -> alert.getDialogPane().lookupButton(ButtonType.OK).requestFocus());

        // Handle the "OK" button click
        alert.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Code to be executed when the "OK" button is clicked
                showComponent("/Views/LearningOverviewView.fxml");
            }
            return null;
        });

        alert.showAndWait();
    }

    private void checkAnswer() {
        Question question = listQuestion.get(currentQuestionIndex);
        if (answer.isEmpty()) {
            return;
        }
        super.countdownTimer.stop();
        if (answer.equals(question.getCorrectAnswer())) {
            handleCorrectAnswer();
        } else {
            handleIncorrectAnswer();
        }
    }

    private void handleIncorrectAnswer() {
        long timeSpend = 30 - secondsRemaining.get();
        Duration duration = Duration.ofSeconds(timeSpend);
        listQuestion.get(currentQuestionIndex).setAnsIsCorrect(false);
        listQuestion.get(currentQuestionIndex).setFinishedTime(duration);

        setQuestionProgress(currentQuestionIndex + 1, incorrectQuestion);
        createAnimation(questionAndAnswerPane, false);
        updateScore(incorrectScore);
    }

    private void handleCorrectAnswer() {
        long timeSpend = 30 - secondsRemaining.get();
        Duration duration = Duration.ofSeconds(timeSpend);
        listQuestion.get(currentQuestionIndex).setAnsIsCorrect(true);
        listQuestion.get(currentQuestionIndex).setFinishedTime(duration);

        setQuestionProgress(currentQuestionIndex + 1, correctQuestion);
        updateScore(correctScore);
        createAnimation(questionAndAnswerPane, true);
    }

    private void updateScore(int correctScore) {
        score += correctScore;
        scoreLabel.setText(score + "/100");
    }

    private void setupQuestion() {
        Question.readQuestionFile(listQuestionData);
        pickQuestions();
        currentQuestionIndex = 0;
        questionTitle.setText("Câu hỏi " + (currentQuestionIndex + 1) + ":");
        showQuestion(listQuestion.get(0));
    }

    private void showQuestion(Question question) {
        //Enable buttons
        answerBtnA.setDisable(false);
        answerBtnB.setDisable(false);
        answerBtnC.setDisable(false);
        answerBtnD.setDisable(false);

        questionText.setText(question.getQuestionTitle());
        answerBtnA.setText(question.getAnswerA());
        answerBtnB.setText(question.getAnswerB());
        answerBtnC.setText(question.getAnswerC());
        answerBtnD.setText(question.getAnswerD());
        setQuestionProgress(currentQuestionIndex + 1, currentQuestion);
        setupCountdownTimer();
    }

    private void setupCountdownTimer() {
        secondsRemaining = new AtomicInteger(countDownTime);
        super.countdownTimer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), (ActionEvent event) -> {
            secondsRemaining.getAndDecrement();
            countdownLabel.setText(String.valueOf(secondsRemaining.get()) + "s");

            if (secondsRemaining.get() <= 0) {
                super.countdownTimer.stop();
                countdownLabel.setText("Time's up!");
                handleTimeUp();
            }
        }));

        super.countdownTimer.setCycleCount(Timeline.INDEFINITE);
        super.countdownTimer.play();
    }

    private void handleTimeUp() {
        if (answer.isEmpty()) {
            handleIncorrectAnswer();
        } else {
            checkAnswer();
        }
    }

    private void createAnimation(Pane container, Boolean isCorrect) {
        //Disable buttons
        answerBtnA.setDisable(true);
        answerBtnB.setDisable(true);
        answerBtnC.setDisable(true);
        answerBtnD.setDisable(true);
        nextBtn.setDisable(true);

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(PATH_TO_IMAGE_FIREWORK);
        javafx.scene.image.Image originalImage = new javafx.scene.image.Image(inputStream);

        if (isCorrect) {
            // Tải hình ảnh từ tài nguyên
            inputStream = classLoader.getResourceAsStream(PATH_TO_IMAGE_FIREWORK);
            originalImage = new javafx.scene.image.Image(inputStream);
            correctAudioMedia = new Media(classLoader.getResource(PATH_TO_CORRECT_CHEERING).toExternalForm());
            correctAudioMediaPlayer = new MediaPlayer(correctAudioMedia);
        } else {
            // Tải hình ảnh từ tài nguyên
            inputStream = classLoader.getResourceAsStream(PATH_TO_IMAGE_SAD);
            originalImage = new javafx.scene.image.Image(inputStream);
            correctAudioMedia = new Media(classLoader.getResource(PATH_TO_INCORRECT_SOUND).toExternalForm());
            correctAudioMediaPlayer = new MediaPlayer(correctAudioMedia);
        }

        correctAudioMediaPlayer.play(); // Phát âm thanh hoan hô

        // Tạo một số lượng hạt pháo hoa
        int numFireworks = 40;

        for (int i = 0; i < numFireworks; i++) {
            Timeline timeline = new Timeline();

            // Tạo một ImageView với hình ảnh pháo hoa
            ImageView firework = new ImageView(originalImage);

            // Đặt tỷ lệ thu nhỏ (scale) hình ảnh
            firework.setPreserveRatio(true);
            firework.setFitWidth(originalImage.getWidth() / 10); // Thu nhỏ ảnh lại 10 lần

            // Đặt vị trí ban đầu của hạt pháo hoa
            double initialX = Math.random() * container.getWidth();
            double initialY = Math.random() * container.getHeight();
            firework.setLayoutX(initialX);
            firework.setLayoutY(initialY);

            // Thêm hạt pháo hoa vào container
            container.getChildren().add(firework);

            // KeyValues cho lắc lắc
            KeyValue keyValueScaleX = new KeyValue(firework.scaleXProperty(), 2); // Kích thước X
            KeyValue keyValueScaleY = new KeyValue(firework.scaleYProperty(), 2); // Kích thước Y
            KeyValue keyValueOpacity = new KeyValue(firework.opacityProperty(), 0); // Độ trong suốt
            KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.seconds(4), keyValueScaleX, keyValueScaleY, keyValueOpacity);
            timeline.getKeyFrames().add(keyFrame);

            // KeyValues cho lắc lắc
            for (int j = 0; j < 10; j++) {
                double offsetX = Math.random() * 10 - 5; // Tạo lắc lắc ngẫu nhiên trong khoảng [-5, 5]
                double offsetY = Math.random() * 10 - 5;
                KeyFrame keyFrameShake = new KeyFrame(javafx.util.Duration.millis(100 * j),
                        new KeyValue(firework.layoutXProperty(), initialX + offsetX),
                        new KeyValue(firework.layoutYProperty(), initialY + offsetY)
                );
                timeline.getKeyFrames().add(keyFrameShake);
            }

            /// Xóa hạt pháo hoa sau khi hoàn thành animation
            timeline.setOnFinished(event -> {
                container.getChildren().remove(firework);
            });

            // Bắt đầu animation
            timeline.play();

            if (i == 0) {
                mainTimeLine = timeline;
            }
        }

        mainTimeLine.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                correctAudioMediaPlayer.stop(); // Dừng phát âm thanh sau khi hiệu ứng hoàn thành

                //Enable buttons
                answerBtnA.setDisable(false);
                answerBtnB.setDisable(false);
                answerBtnC.setDisable(false);
                answerBtnD.setDisable(false);
                nextBtn.setDisable(false);

                answerBtnA.setSelected(false);
                answerBtnB.setSelected(false);
                answerBtnC.setSelected(false);
                answerBtnD.setSelected(false);

                nextQuestion();
                answer = "";
                isHandledFinishAnimation = true;
            }
        });
    }

    private void setQuestionProgress(int questionNumber, Paint color) {
        switch (questionNumber) {
            case 1: {
                circle1.setStroke(color);
                circle1.setFill(color);
                progressText1.setTextFill(Color.WHITE);
                break;
            }
            case 2: {
                circle2.setStroke(color);
                circle2.setFill(color);
                progressText2.setTextFill(Color.WHITE);
                break;
            }
            case 3: {
                circle3.setStroke(color);
                circle3.setFill(color);
                progressText3.setTextFill(Color.WHITE);
                break;
            }
            case 4: {
                circle4.setStroke(color);
                circle4.setFill(color);
                progressText4.setTextFill(Color.WHITE);
                break;
            }
            case 5: {
                circle5.setStroke(color);
                circle5.setFill(color);
                progressText5.setTextFill(Color.WHITE);
                break;
            }
            case 6: {
                circle6.setStroke(color);
                circle6.setFill(color);
                progressText6.setTextFill(Color.WHITE);
                break;
            }
            case 7: {
                circle7.setStroke(color);
                circle7.setFill(color);
                progressText7.setTextFill(Color.WHITE);
                break;
            }
            case 8: {
                circle8.setStroke(color);
                circle8.setFill(color);
                progressText8.setTextFill(Color.WHITE);
                break;
            }
            case 9: {
                circle9.setStroke(color);
                circle9.setFill(color);
                progressText9.setTextFill(Color.WHITE);
                break;
            }
            case 10: {
                circle10.setStroke(color);
                circle10.setFill(color);
                progressText10.setTextFill(Color.WHITE);
                break;
            }
            default: {

            }
        }
    }

}
