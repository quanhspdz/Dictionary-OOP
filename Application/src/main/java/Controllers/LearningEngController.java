package Controllers;

import Models.Question;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Constant.Constant.*;

public class LearningEngController implements Initializable {

    @FXML
    public AnchorPane bodyContainer, headerContainer;
    @FXML
    public Pane questionAndAnswerPane;
    @FXML
    private RadioButton answerBtnA, answerBtnB, answerBtnC, answerBtnD;
    @FXML
    private Button nextBtn;
    @FXML
    private Circle circle1, circle2, circle3, circle4, circle5, circle6,
            circle7, circle8, circle9, circle10;
    @FXML
    private Label progressText1, progressText2, progressText3, progressText4,
            progressText5, progressText6, progressText7, progressText8, progressText9, progressText10,
            questionText, questionTitle;

    private Media correctAudioMedia;
    private MediaPlayer correctAudioMediaPlayer;
    private final Paint currentQuestion = Color.rgb(152, 206, 243),
            correctQuestion = Color.rgb(166, 232, 58),
            incorrectQuestion = Color.rgb(255, 216, 0);

    private ArrayList<Question> listQuestion = new ArrayList<>();
    private int currentQuestionIndex;

    private String answer = "";

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
    }

    private void checkAnswer() {
        Question question = listQuestion.get(currentQuestionIndex);
        if (answer.equals(question.getCorrectAnswer())) {
            handleCorrectAnswer();
        } else {
            handleIncorrectAnswer();
        }
    }

    private void handleIncorrectAnswer() {
        createAnimation(questionAndAnswerPane, false);
    }

    private void handleCorrectAnswer() {
        createAnimation(questionAndAnswerPane, true);
    }

    private void setupQuestion() {
        Question.readQuestionFile(listQuestion);
        currentQuestionIndex = 1;
        questionTitle.setText("Câu hỏi " + currentQuestionIndex + ":");
        showQuestion(listQuestion.get(0));
    }

    private void showQuestion(Question question) {
        questionText.setText(question.getQuestionTitle());
        answerBtnA.setText(question.getAnswerA());
        answerBtnB.setText(question.getAnswerB());
        answerBtnC.setText(question.getAnswerC());
        answerBtnD.setText(question.getAnswerD());
    }

    private void createAnimation(Pane container, Boolean isCorrect) {
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
        int numFireworks = 30;

        for (int i = 0; i < numFireworks; i++) {
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

            // Tạo animation cho hạt pháo hoa
            Timeline timeline = new Timeline();

            // KeyValues cho lắc lắc
            KeyValue keyValueScaleX = new KeyValue(firework.scaleXProperty(), 2); // Kích thước X
            KeyValue keyValueScaleY = new KeyValue(firework.scaleYProperty(), 2); // Kích thước Y
            KeyValue keyValueOpacity = new KeyValue(firework.opacityProperty(), 0); // Độ trong suốt
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(4), keyValueScaleX, keyValueScaleY, keyValueOpacity);
            timeline.getKeyFrames().add(keyFrame);

            // KeyValues cho lắc lắc
            for (int j = 0; j < 10; j++) {
                double offsetX = Math.random() * 10 - 5; // Tạo lắc lắc ngẫu nhiên trong khoảng [-5, 5]
                double offsetY = Math.random() * 10 - 5;
                KeyFrame keyFrameShake = new KeyFrame(Duration.millis(100 * j),
                        new KeyValue(firework.layoutXProperty(), initialX + offsetX),
                        new KeyValue(firework.layoutYProperty(), initialY + offsetY)
                );
                timeline.getKeyFrames().add(keyFrameShake);
            }

            /// Xóa hạt pháo hoa sau khi hoàn thành animation
            timeline.setOnFinished(event -> {
                container.getChildren().remove(firework);
                correctAudioMediaPlayer.stop(); // Dừng phát âm thanh sau khi hiệu ứng hoàn thành

                //Enable buttons
                answerBtnA.setDisable(false);
                answerBtnB.setDisable(false);
                answerBtnC.setDisable(false);
                answerBtnD.setDisable(false);
                nextBtn.setDisable(false);
            });

            //Disable buttons
            answerBtnA.setDisable(true);
            answerBtnB.setDisable(true);
            answerBtnC.setDisable(true);
            answerBtnD.setDisable(true);
            nextBtn.setDisable(true);

            // Bắt đầu animation
            timeline.play();
        }
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
