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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static App.DictionaryApp.*;
import static Constant.Constant.*;
import static Constant.Key.apiKey;

public class SearchWordController extends BaseController implements Initializable {

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
                    textToSpeechGoogle(word);
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

    public static void textToSpeechGoogle(String text) {
        try {
            // Build the URL for the Text-to-Speech API
            URL url = new URL("https://texttospeech.googleapis.com/v1/text:synthesize?key=" + apiKey);

            // Create a connection to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

//            String language = "vi-VN"; // Mã ngôn ngữ tiếng Việt
//            String voice = "vi-VN-Wavenet-A"; // Tên giọng nữ tiếng Việt

            String language = "en-US";
            String voice = "en-US-Studio-O";

            // Build the JSON payload
            String jsonInputString = "{\"input\": {\"text\":\"" + text
                    + "\"}, \"voice\": {\"languageCode\":\""+ language +"\",\"name\":\""+ voice +"\"}, " +
                    "\"audioConfig\": {\"audioEncoding\":\"MP3\"}}";

            // Write the JSON payload to the connection
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the audio contents from the response
                try (InputStream inputStream = connection.getInputStream()) {
                    byte[] audioBytes = inputStream.readAllBytes();
                    playAudio(audioBytes);
                }
            } else {
                System.out.println("Error: " + responseCode);
                textToSpeech(text);
            }

        } catch (IOException e) {
            e.printStackTrace();
            textToSpeech(text);
        }
    }

    private static void saveAudioFile(InputStream inputStream) throws IOException, ParseException {
        // Đọc nội dung của tệp JSON thành chuỗi
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        StringBuilder contentBuilder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            contentBuilder.append((char) c);
        }

        String content = contentBuilder.toString();
//        JSONParser parser = new JSONParser();
//        JSONObject object = (JSONObject) parser.parse(content);
//        String base64 = (String) object.get("audioContent");
        System.out.println("BASE64: " + content);
    }

    private static void writeAudioToFile(byte[] audioData, String filename) throws IOException {
        try (FileOutputStream output = new FileOutputStream(filename)) {
            output.write(audioData);
            System.out.println("Audio content written to file \"" + filename + "\"");
        }
    }

    public static void playAudio(byte[] audioBytes) {
        try {
            byte[] decodedBytes = decodeResponse(audioBytes);
            // Tạo tệp tạm thời từ mảng byte
            File audioTempFile = new File(TEMP_AUDIO_FILE);
            audioTempFile.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(audioTempFile)) {
                fos.write(decodedBytes);
            }

            // Tạo Media từ đường dẫn tạm thời
            Media media = new Media(audioTempFile.toURI().toString());

            // Tạo MediaPlayer từ Media
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Phát âm thanh
            mediaPlayer.play();

            // Đợi cho đến khi âm thanh phát xong và sau đó giải phóng tài nguyên
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                audioTempFile.delete();
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static byte[] decodeResponse(byte[] audioBytes) throws IOException, ParseException {
        // Tạo tệp tạm thời từ mảng byte
        File tempFile = new File(TEMP_TXT_FILE);
        tempFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(audioBytes);
        }

        // Đọc nội dung của tệp JSON thành chuỗi
        InputStream inputStream = new FileInputStream(tempFile.getPath());
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        StringBuilder contentBuilder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            contentBuilder.append((char) c);
        }

        String content = contentBuilder.toString();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(content);
        String base64 = (String) object.get("audioContent");
        tempFile.delete(); // Xóa tệp tạm thời sau khi đã sử dụng

        return Base64.getDecoder().decode(base64);
    }

        public static void main(String[] args) {
        textToSpeechGoogle("Hello World");
    }
}
