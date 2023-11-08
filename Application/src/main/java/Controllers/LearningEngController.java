package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
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

    private Media correctAudioMedia;
    private MediaPlayer correctAudioMediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ClassLoader classLoader = getClass().getClassLoader();
        //Tải audio từ tài nguyên
        correctAudioMedia = new Media(classLoader.getResource(PATH_TO_CORRECT_CHEERING).toExternalForm());
        correctAudioMediaPlayer = new MediaPlayer(correctAudioMedia);

        answerBtnA.setOnAction(event -> {
            answerBtnA.setSelected(true);
            answerBtnB.setSelected(false);
            answerBtnC.setSelected(false);
            answerBtnD.setSelected(false);
        });

        answerBtnB.setOnAction(event -> {
            answerBtnA.setSelected(false);
            answerBtnB.setSelected(true);
            answerBtnC.setSelected(false);
            answerBtnD.setSelected(false);
        });

        answerBtnC.setOnAction(event -> {
            answerBtnA.setSelected(false);
            answerBtnB.setSelected(false);
            answerBtnC.setSelected(true);
            answerBtnD.setSelected(false);
        });

        answerBtnD.setOnAction(event -> {
            answerBtnA.setSelected(false);
            answerBtnB.setSelected(false);
            answerBtnC.setSelected(false);
            answerBtnD.setSelected(true);
        });

        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createFireworkAnimation(questionAndAnswerPane);
            }
        });
    }

    private void createFireworkAnimation(Pane container) {
        ClassLoader classLoader = getClass().getClassLoader();

        correctAudioMediaPlayer.stop(); // Đảm bảo rằng âm thanh sẽ chơi từ đầu
        correctAudioMediaPlayer.play(); // Phát âm thanh hoan hô

        // Tải hình ảnh từ tài nguyên
        InputStream inputStream = classLoader.getResourceAsStream(PATH_TO_IMAGE_FIREWORK);
        javafx.scene.image.Image originalImage = new javafx.scene.image.Image(inputStream);

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
            });

            // Bắt đầu animation
            timeline.play();
        }
    }


}
