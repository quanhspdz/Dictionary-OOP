package Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DictionaryController extends BaseController implements Initializable {
    @FXML
    private Tooltip tooltip1, tooltip2, tooltip3, tooltip4;

    @FXML
    private Button addWordBtn;
    @FXML
    private Button translateBtn;
    @FXML
    private Button searchWordBtn;
    @FXML
    private Button userProfileBtn;
    @FXML
    private Button learningEngBtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchWordBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/SearchWordView.fxml");
            }
        });

        addWordBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/AddWordView.fxml");
            }
        });

        translateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/TranslationView.fxml");
            }
        });

        userProfileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

            }
        });

        learningEngBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/LearningOverviewView.fxml");
            }
        });

        tooltip1.setShowDelay(Duration.seconds(0.3));
        tooltip2.setShowDelay(Duration.seconds(0.3));
        tooltip3.setShowDelay(Duration.seconds(0.3));
        tooltip4.setShowDelay(Duration.seconds(0.3));
        showComponent("/Views/SearchWordView.fxml");
    }
}
