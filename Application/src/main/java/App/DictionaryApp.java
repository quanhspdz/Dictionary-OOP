package App;

import Interfaces.DataLoadedListener;
import Models.Word;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static Constant.Constant.*;

public class DictionaryApp extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    public static Map<String, Word> dataEngVie = new HashMap<>();
    public static Map<String, Word> dataVieEng = new HashMap<>();
    public static Map<String, Word> currentData = new HashMap<>();


    public static boolean isLoadedAllData = false;
    private static List<DataLoadedListener> listeners = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/LoginView.fxml")));
        stage.setTitle("Dictionary Application");
        readData(dataVieEng, DATA_VE_FILE_PATH, EDITED_WORD_VE_FILE);
        readData(dataEngVie, DATA_EV_FILE_PATH, EDITED_WORD_EV_FILE);
//        stage.initStyle(StageStyle.TRANSPARENT);
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void readData(Map<String, Word> data,
                                String DATA_FILE_PATH, String EDITED_WORD_FILE) throws IOException {
        data.clear();

        ClassLoader classLoader = DictionaryApp.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(DATA_FILE_PATH);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(SPLITTING_CHARACTERS);
            String word = parts[0];
            String definition = SPLITTING_CHARACTERS + parts[1];
            Word wordObj = new Word(word, definition);
            data.put(word, wordObj);
        }

        File file = new File(EDITED_WORD_FILE);
        if (file.exists() && file.isFile()) {
            inputStream = new FileInputStream(EDITED_WORD_FILE);
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SPLITTING_CHARACTERS);
                if (parts.length > 1) {
                    String word = parts[0];
                    String definition = SPLITTING_CHARACTERS + parts[1];
                    Word wordObj = new Word(word, definition);
                    data.put(word, wordObj);
                } else {
                    data.remove(parts[0]);
                }

            }
        }

        currentData = data;
        dataLoaded();
    }

    public static void dataLoaded() {
        isLoadedAllData = true;
        notifyListeners();
    }

    public static void addDataLoadedListener(DataLoadedListener listener) {
        listeners.add(listener);
    }

    public void removeDataLoadedListener(DataLoadedListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (DataLoadedListener listener : listeners) {
            listener.onDataLoaded();
        }
    }
}
