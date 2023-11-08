package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class BaseController {
    public void setNode(Node node, AnchorPane container) {
        container.getChildren().clear();
        container.getChildren().add(node);
    }

    public void showComponent(String path, AnchorPane container) {
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
}
