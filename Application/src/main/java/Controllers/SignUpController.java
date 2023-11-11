package Controllers;

import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class SignUpController implements Initializable {

    @FXML
    private Button changePass_backBtn;

    @FXML
    private TextField changePass_cPassword;

    @FXML
    private AnchorPane changePass_form;

    @FXML
    private TextField changePass_password;

    @FXML
    private Button changePass_proceedBtn;

    @FXML
    private TextField forgot_answer;

    @FXML
    private Button forgot_backBtn;

    @FXML
    private AnchorPane forgot_form;

    @FXML
    private Button forgot_proceedBtn;

    @FXML
    private ComboBox<?> forgot_selectQuestion;

    @FXML
    private TextField forgot_username;

    @FXML
    private Button login_btn;

    @FXML
    private Button login_create;

    @FXML
    private Hyperlink login_forgotPassword;

    @FXML
    private AnchorPane login_form;

    @FXML
    private PasswordField login_password;

    @FXML
    private CheckBox login_selectShowPassword;

    @FXML
    private TextField login_username;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField signup_answer;

    @FXML
    private Button signup_btn;

    @FXML
    private TextField signup_cPassword;

    @FXML
    private TextField signup_email;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Button signup_loginAccount;

    @FXML
    private PasswordField signup_password;

    @FXML
    private ComboBox<?> signup_selectQuestion;

    @FXML
    private TextField signup_username;


    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;


    public void showPassword() {

        if (login_selectShowPassword.isSelected()) {
//            login_showPassword.setText(login_password.getText());
//            login_showPassword.setVisible(true);
//            login_password.setVisible(false);
        } else {
//            login_password.setText(login_showPassword.getText());
//            login_showPassword.setVisible(false);
//            login_password.setVisible(true);
        }

    }

    public void forgotPassword() {

        alertMessage alert = new alertMessage();

        if (forgot_username.getText().isEmpty()
                || forgot_selectQuestion.getSelectionModel().getSelectedItem() == null
                || forgot_answer.getText().isEmpty()) {
            alert.errorMessage("Please fill all blank fields");
        }
    }


    public void forgotListQuestion() {

        List<String> listQ = new ArrayList<>();

        for (String data : questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        forgot_selectQuestion.setItems(listData);

    }

    public void register() {

        alertMessage alert = new alertMessage();

        // CHECK IF WE HAVE EMPTY FIELDS
        if (signup_email.getText().isEmpty() || signup_username.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()
                || signup_selectQuestion.getSelectionModel().getSelectedItem() == null
                || signup_answer.getText().isEmpty()) {
            alert.errorMessage("All fields are necessary to be filled");
        } else if (signup_password.getText() == signup_cPassword.getText()) {
            // CHECK IF THE VALUE OF PASSWORD FIELDS IS EQUAL TO CONFIRM PASSWORD
            alert.errorMessage("Password does not match");
        } else if (signup_password.getText().length() < 8) {
            // CHECK IF THE LENGTH OF PASSWORD VALUE IS LESS THAN TO 8
            //, WE WILL CHECK THE CHARACTERS OF PASSWORD
            alert.errorMessage("Invalid Password, at least 8 characters needed");
        }
    }

    // TO CLEAR ALL FIELDS OF REGISTRATION FORM
    public void registerClearFields() {
        signup_email.setText("");
        signup_username.setText("");
        signup_password.setText("");
        signup_cPassword.setText("");
        signup_selectQuestion.getSelectionModel().clearSelection();
        signup_answer.setText("");
    }

    public void changePassword() {

        alertMessage alert = new alertMessage();
        // CHECK ALL FIELDS IF EMPTY OR NOT
        if (changePass_password.getText().isEmpty() || changePass_cPassword.getText().isEmpty()) {
            alert.errorMessage("Please fill all blank fields");
        } else if (!changePass_password.getText().equals(changePass_cPassword.getText())) {
            // CHECK IF THE PASSWORD AND CONFIRMATION ARE NOT MATCH
            alert.errorMessage("Password does not match");
        } else if (changePass_password.getText().length() < 8) {
            // CHECK IF THE LENGTH OF PASSWORD IS LESS THAN TO 8
            alert.errorMessage("Invalid Password, at least 8 characters needed");
        }

    }

    public void switchForm(ActionEvent event) {

        // THE REGISTRATION FORM WILL BE VISIBLE
        if (event.getSource() == signup_loginAccount || event.getSource() == forgot_backBtn) {
            signup_form.setVisible(false);
            login_form.setVisible(true);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_create) { // THE LOGIN FORM WILL BE VISIBLE
            signup_form.setVisible(true);
            login_form.setVisible(false);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_forgotPassword) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);
            // TO SHOW THE DATA TO OUR COMBOBOX
            forgotListQuestion();
        } else if (event.getSource() == changePass_backBtn) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);
        }

    }

    private String[] questionList = {"What is your favorite food?", "What is your favorite color?",
            "What is the name of your pet?", "What is your most favorite sport?"};

    public void questions() {
        List<String> listQ = new ArrayList<>();

        for (String data : questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        signup_selectQuestion.setItems(listData);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        questions();

        forgotListQuestion();
    }

}


