package Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class LearningOverviewController extends BaseController implements Initializable {

    @FXML
    ImageView imageRanking;
    @FXML
    Label labelRanking;
    @FXML
    ListView<String> listViewRanking;
    @FXML
    PieChart pieChartQuestion;
    @FXML
    LineChart<String, String> lineChartTime;
    @FXML
    Button btnOffline, btnPK;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOffline.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showComponent("/Views/LearningEngView.fxml");
            }
        });
    }
}
