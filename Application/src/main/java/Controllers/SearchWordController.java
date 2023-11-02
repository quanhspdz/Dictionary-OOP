package Controllers;

import Models.Word;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SearchWordController implements Initializable {
    private static final String DATA_FILE_PATH = "Application/src/main/data/E_V.txt";
    private static final String SPLITTING_CHARACTERS = "<html>";
    private Map<String, Word> data = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            readData();
            loadWordList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadWordList() {
        this.wordListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Word selectedWord = data.get(newValue.trim());
                    String definition = selectedWord.getDef();
                    this.selectedWord.setText(selectedWord.getWord());
                    definitionWebView.getEngine().loadContent(definition, "text/html");
                }
        );
        // Chuyển danh sách từ Map thành danh sách có thứ tự
        List<String> sortedWords = new ArrayList<>(data.keySet());

        // Sắp xếp danh sách các từ theo thứ tự bảng chữ cái (alpha)
        Collections.sort(sortedWords);

        // Đặt danh sách đã sắp xếp vào wordListView
        this.wordListView.getItems().setAll(sortedWords);
    }

    public void readData() throws IOException {
        FileReader fis = new FileReader(DATA_FILE_PATH);
        BufferedReader br = new BufferedReader(fis);
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(SPLITTING_CHARACTERS);
            String word = parts[0];
            String definition = SPLITTING_CHARACTERS + parts[1];
            Word wordObj = new Word(word, definition);
            data.put(word, wordObj);
        }
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
    private Label selectedWord, headerList, notAvailableAlert;

    @FXML
    private TextArea explanation;

    @FXML
    private ListView<String> wordListView;

    @FXML
    private Pane headerOfExplanation;

    @FXML
    public WebView definitionWebView;
}
