package Controllers;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

import static Constant.Key.apiKey;
import static Controllers.SearchWordController.*;

public class TranslationController extends BaseController implements Initializable {
    private String sourceLanguage = "en";
    private String targetLanguage = "vi";
    private boolean isToVietnameseLang = true;

    private Translate translate;

    @FXML
    private TextArea translationTextField, sourceTextField;

    @FXML
    private Button translateBtn, clearBtn, btnListenSource, btnListenTarget;

    @FXML
    private Label sourceLangLabel , targetLangLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translate =  TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
        clearBtn.setVisible(false);
        btnListenSource.setDisable(true);
        btnListenTarget.setDisable(true);

        translateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleTranslation();
            }
        });

        sourceTextField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                translateBtn.setDisable(sourceTextField.getText().trim().isEmpty());
                btnListenSource.setDisable(sourceTextField.getText().trim().isEmpty());
                clearBtn.setVisible(!sourceTextField.getText().trim().isEmpty());
            }
        });

        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sourceTextField.clear();
                translateBtn.setDisable(true);
            }
        });

        btnListenSource.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleTextToSpeech(
                        sourceTextField.getText(),
                        sourceLanguage);
            }
        });

        btnListenTarget.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleTextToSpeech(
                        translationTextField.getText(),
                        targetLanguage);
            }
        });

        translateBtn.setDisable(true);
        translationTextField.setEditable(false);
    }

    private void handleTextToSpeech(String text, String language) {
        if (language.equals("en")) {
            textToSpeechGoogle(text, engLangCode, voiceEng);
        } else {
            textToSpeechGoogle(text, vieLangCode, voiceVie);
        }
    }

    private void handleTranslation() {
        String sourceText = sourceTextField.getText().trim();
        Translation translation =
                translate.translate(
                        sourceText,
                        Translate.TranslateOption.sourceLanguage(sourceLanguage),
                        Translate.TranslateOption.targetLanguage(targetLanguage),
                        // Use "base" for standard edition, "nmt" for the premium model.
                        Translate.TranslateOption.model("nmt"));

        translationTextField.setText(translation.getTranslatedText());
        btnListenTarget.setDisable(translationTextField.getText().trim().isEmpty());
    }

    @FXML
    private void handleOnClickSwitchToggle() {
        sourceTextField.clear();
        translationTextField.clear();
        if (isToVietnameseLang) {
            sourceLangLabel.setText("Tiếng Việt");
            targetLangLabel.setText("Tiếng Anh");
            sourceLanguage = "vi";
            targetLanguage = "en";
        } else {
            targetLangLabel.setText("Tiếng Việt");
            sourceLangLabel.setText("Tiếng Anh");
            sourceLanguage = "en";
            targetLanguage = "vi";
        }
        isToVietnameseLang = !isToVietnameseLang;
    }
}
