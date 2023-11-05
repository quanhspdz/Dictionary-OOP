package Controllers;

import App.DictionaryApp;
import DialogAlert.AlertDialog;
import Interfaces.DataLoadedListener;
import Models.Word;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static App.DictionaryApp.*;
import static Constant.Constant.*;

public class SearchWordController implements Initializable {

    @FXML
    private TextField searchTerm;

    @FXML
    private Button cancelBtn, saveBtn, textToSpeechBtn,
            editDefinitionBtn, deleteWordBtn;

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

    private WebEngine definitionWebEngine;

    private Word currentSelectedWord;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (data.isEmpty()) {
            DictionaryApp.addDataLoadedListener(new DataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    loadWordList(); // Thực hiện loadWordList() khi dữ liệu đã được tải
                }
            });
        } else {
            loadWordList();
        }

        notAvailableAlert.setVisible(false);
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);

        searchTerm.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String searchKey = searchTerm.getText();
                if (searchKey.isEmpty()) {
                    showDefaultListView();
                    cancelBtn.setVisible(false);
                    notAvailableAlert.setVisible(false);
                } else {
                    cancelBtn.setVisible(true);
                    handleSearchOnKeyTyped(searchKey);
                }
            }
        });

        textToSpeechBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String word = selectedWord.getText();
                if (word != null) {
                    textToSpeech(word);
                }
            }
        });

        editDefinitionBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentSelectedWord != null) {
                    handleEditDefinition();
                }
            }
        });

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    handleSaveDefinitionEdit();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        deleteWordBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentSelectedWord != null) {
                    handleDeleteWord();
                }
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchTerm.clear();
                showDefaultListView();
            }
        });
    }

    private void handleDeleteWord() {
        AlertDialog alertDialog = new AlertDialog();
        Alert warningAlert = alertDialog.warningAlertDialog(
                "Xoá từ " + currentSelectedWord.getWord(),
                "Bạn có chắc chắn muốn xoá từ này không?"
        );
        warningAlert.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = warningAlert.showAndWait();
        if (option.get() == ButtonType.OK) {
            try {
                deleteWord(currentSelectedWord);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            alertDialog.showInfoAlertDialog(
                    "Thông tin",
                    "Đã xoá thành công"
            );
        } else {
            alertDialog.showInfoAlertDialog(
                    "Thông tin",
                    "Đã huỷ xoá"
            );
        }
    }

    private void deleteWord(Word currentSelectedWord) throws IOException {
        data.remove(currentSelectedWord.getWord());
        showDefaultListView();
        currentSelectedWord.setDef("");
        selectedWord.setText("");
        definitionWebEngine.loadContent("", "text/html");
        saveWordToFile(currentSelectedWord);
        this.currentSelectedWord = null;
    }

    private void handleSaveDefinitionEdit() throws IOException {
        String newDef = (String) definitionWebEngine.executeScript("document.documentElement.outerHTML");
        currentSelectedWord.setDef(newDef);
        saveWordToFile(currentSelectedWord);
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.showInfoAlertDialog(
                "Thông tin",
                "Đã lưu chỉnh sửa!"
        );
    }

    private void saveWordToFile(Word currentSelectedWord) throws IOException {
        // Tạo một đối tượng File cho tệp dữ liệu
        File file = new File(EDITED_WORD_FILE);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {

            // Ghi dữ liệu vào tệp
            String wordAndDefinition = currentSelectedWord.getWord()
                    + SPLITTING_CHARACTERS + currentSelectedWord.getDef();
            writer.write(wordAndDefinition);
            writer.newLine(); // Xuống dòng mới cho từ tiếp theo (nếu cần)
        }
    }


    private void handleEditDefinition() {
        // Thêm thuộc tính contenteditable="true" vào mã HTML
        saveBtn.setVisible(true);
        String definition = currentSelectedWord.getDef();
        definition = definition.replaceFirst("<html", "<html contenteditable=\"true\"");
        definitionWebEngine.loadContent(definition, "text/html");
    }

    public void loadWordList() {
        this.wordListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Đảm bảo rằng newValue không null
                Word selectedWord = data.get(newValue.trim());
                currentSelectedWord = selectedWord;
                saveBtn.setVisible(false);
                String definition = selectedWord.getDef();
                this.selectedWord.setText(selectedWord.getWord());

                definitionWebEngine = definitionWebView.getEngine();
                definitionWebEngine.loadContent(definition, "text/html");
            }
        });

        showDefaultListView();
    }

    private void showDefaultListView() {
        // Chuyển danh sách từ Map thành danh sách có thứ tự
        List<String> sortedWords = new ArrayList<>(data.keySet());

        // Sắp xếp danh sách các từ theo thứ tự bảng chữ cái (alpha)
        Collections.sort(sortedWords);

        // Đặt danh sách đã sắp xếp vào wordListView
        this.wordListView.getItems().setAll(sortedWords);
    }

    private void showSearchResultListView(List<String> searchResultList) {
        // Sắp xếp danh sách các từ theo thứ tự bảng chữ cái (alpha)
        Collections.sort(searchResultList);

        // Đặt danh sách đã sắp xếp vào wordListView
        this.wordListView.getItems().setAll(searchResultList);
    }

    @FXML
    private void handleSearchOnKeyTyped(String searchKey) {
        List<String> searchResultList = new ArrayList<>();
        // Chuyển sang chữ thường để tìm kiếm không phân biệt chữ hoa/chữ thường
        searchKey = searchKey.trim().toLowerCase();

        for (String key : data.keySet()) {
            if (key.toLowerCase().startsWith(searchKey)) {
                searchResultList.add(key);
            }
        }

        if (searchResultList.isEmpty()) {
            notAvailableAlert.setVisible(true);
            showDefaultListView();
        } else {
            notAvailableAlert.setVisible(false);
            showSearchResultListView(searchResultList);
        }
    }

    public static void textToSpeech(String text) {
        // Khởi tạo FreeTTS
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice("kevin16");

        if (voice != null) {
            voice.allocate();
            voice.speak(text);
        } else {
            System.out.println("Không thể tìm thấy giọng đọc.");
        }
    }
}
