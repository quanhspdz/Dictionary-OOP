package Controllers;

import App.DictionaryApp;
import Interfaces.DataLoadedListener;
import Models.Word;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static App.DictionaryApp.data;

public class SearchWordController implements Initializable {
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

        searchTerm.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String searchKey = searchTerm.getText();
                if (searchKey.isEmpty()) {
                    cancelBtn.setVisible(false);
                    showDefaultListView();
                } else {
                    cancelBtn.setVisible(true);
                    handleSearchOnKeyTyped(searchKey);
                }
            }
        });
    }

    public void loadWordList() {
        this.wordListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Đảm bảo rằng newValue không null
                Word selectedWord = data.get(newValue.trim());
                String definition = selectedWord.getDef();
                this.selectedWord.setText(selectedWord.getWord());
                definitionWebView.getEngine().loadContent(definition, "text/html");
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
        searchKey = searchKey.toLowerCase();

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
