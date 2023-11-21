package Controllers;

import App.DictionaryApp;
import DialogAlert.AlertDialog;
import Models.Word;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static App.DictionaryApp.data;
import static Constant.Constant.EDITED_WORD_FILE;
import static Constant.Constant.SPLITTING_CHARACTERS;

public class AddWordController extends BaseController implements Initializable {

    @FXML
    private Button addBtn, deleteDefinitionInput, deleteWordInputBtn;
    @FXML
    private TextField newWordInput;
    @FXML
    private TextArea wordDefinitionInput;
    @FXML
    private Label successAlert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        successAlert.setVisible(false);
        deleteDefinitionInput.setVisible(false);
        deleteWordInputBtn.setVisible(false);

        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    handleAddNewWord();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        newWordInput.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String input = newWordInput.getText();
                if (input.isEmpty()) {
                    successAlert.setVisible(false);
                    deleteWordInputBtn.setVisible(false);
                } else {
                    deleteWordInputBtn.setVisible(true);
                }
            }
        });

        wordDefinitionInput.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String input = wordDefinitionInput.getText();
                if (input.isEmpty()) {
                    successAlert.setVisible(false);
                    deleteDefinitionInput.setVisible(false);
                } else {
                    deleteDefinitionInput.setVisible(true);
                }
            }
        });

        deleteWordInputBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newWordInput.clear();
                successAlert.setVisible(false);
            }
        });

        deleteDefinitionInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                wordDefinitionInput.clear();
                successAlert.setVisible(false);
            }
        });
    }

    private void handleAddNewWord() throws IOException {
        AlertDialog alertDialog = new AlertDialog();
        String wordString = newWordInput.getText().trim();
        String definition = wordDefinitionInput.getText().trim();

        if (wordString.isEmpty()) {
            alertDialog.showInfoAlertDialog(
                    "Thông báo",
                    "Bạn chưa nhập từ tiếng Anh!"
            );
        } else if (definition.isEmpty()) {
            alertDialog.showInfoAlertDialog(
                    "Thông báo",
                    "Bạn chưa nhập giải nghĩa của từ!"
            );
        } else {
            Word word = new Word(wordString, definition);
            addNewWord(word);
            successAlert.setVisible(true);
        }
    }

    private void addNewWord(Word word) throws IOException {
        //Thêm từ mới vào trong hashmap data
        data.put(word.getWord(), word);

        // Tạo một đối tượng File cho tệp dữ liệu
        File file = new File(EDITED_WORD_FILE);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {

            // Ghi dữ liệu vào tệp
            String wordAndDefinition = word.getWord()
                    + SPLITTING_CHARACTERS + word.getDef();
            writer.write(wordAndDefinition);
            writer.newLine(); // Xuống dòng mới cho từ tiếp theo (nếu cần)
        }
    }
}
