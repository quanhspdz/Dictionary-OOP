package Controllers;

import Models.User;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseController {
    @FXML
    AnchorPane container;

    protected static Timeline countdownTimer;

    protected static User user;

    public static HashMap<String, User> mapUsers = new HashMap<>();
    public static ArrayList<User> listUsers = new ArrayList<>();

    public void setNode(Node node, AnchorPane container) {
        container.getChildren().clear();
        container.getChildren().add(node);
    }

    public void showComponent(String path) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            AnchorPane component = loader.load();

            // Sử dụng thuộc tính ràng buộc (constraints) để thiết lập kích thước và vị trí của component
            AnchorPane.setTopAnchor(component, 0.0);
            AnchorPane.setRightAnchor(component, 0.0);
            AnchorPane.setBottomAnchor(component, 0.0);
            AnchorPane.setLeftAnchor(component, 0.0);

            setNode(component, container);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        BaseController.user = user;
    }
}
