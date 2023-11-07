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

public class TranslationController implements Initializable {
    private String sourceLanguage = "en";
    private String targetLanguage = "vi";
    private boolean isToVietnameseLang = true;

    private Translate translate;

    @FXML
    private TextArea translationTextField, sourceTextField;

    @FXML
    private Button translateBtn, clearBtn;

    @FXML
    private Label englishLabel , vietnameseLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translate =  TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
        clearBtn.setVisible(false);

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

        translateBtn.setDisable(true);
        translationTextField.setEditable(false);
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
    }

    @FXML
    private void handleOnClickSwitchToggle() {
        sourceTextField.clear();
        translationTextField.clear();
        if (isToVietnameseLang) {
            englishLabel.setLayoutX(426);
            vietnameseLabel.setLayoutX(104);
            sourceLanguage = "vi";
            targetLanguage = "en";
        } else {
            englishLabel.setLayoutX(100);
            vietnameseLabel.setLayoutX(426);
            sourceLanguage = "en";
            targetLanguage = "vi";
        }
        isToVietnameseLang = !isToVietnameseLang;
    }
}
