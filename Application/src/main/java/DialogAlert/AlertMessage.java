package DialogAlert;

import javafx.scene.control.Alert;

public class AlertMessage {
    private Alert alert;

    public void errorMessage(String message) {

        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Có lỗi xảy ra");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    public void successMessage(String message) {

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
}
