package Controllers;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SearcherController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void handleMouseClickAWord(MouseEvent arg0) {

    }

    @FXML
    private void handleClickEditBtn() {

    }

    @FXML
    private void handleClickSoundBtn() {

    }

    @FXML
    private void handleClickSaveBtn() {

    }

    @FXML
    private void handleClickDeleteBtn() {

    }

    private void refreshAfterDeleting() {

    }

    private void setListDefault(int index) {

    }

    @FXML
    private TextField searchTerm;

    @FXML
    private Button cancelBtn, saveBtn;

    @FXML
    private Label englishWord, headerList, notAvailableAlert;

    @FXML
    private TextArea explanation;

    @FXML
    private ListView<String> listResults;

    @FXML
    private Pane headerOfExplanation;
}
