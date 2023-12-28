package task.finance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;


import java.io.IOException;
import java.sql.SQLException;

public class signUpController {

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button signUpButton;

    @FXML
    private CheckBox signUpCheckBoxMan;

    @FXML
    private CheckBox signUpCheckBoxWoman;

    @FXML
    private TextField signUpName;

    @FXML
    private TextField signUpSurname;
    @FXML
    private Label messageLabel;


    @FXML
    void initialize() {
        signUpButton.setOnAction(event -> {
            try {
                signUpNewUser();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private void signUpNewUser() throws SQLException, ClassNotFoundException {
        if (signUpName.getText().isEmpty() ||
                signUpSurname.getText().isEmpty() ||
                login_field.getText().isEmpty() ||
                password_field.getText().isEmpty() ||
                (!signUpCheckBoxWoman.isSelected() && !signUpCheckBoxMan.isSelected())) {

            messageLabel.setText("All fields are required to fill in.");
            return;
        }
        DatabaseHandler dbHandler = new DatabaseHandler();
        messageLabel.setText("");
        if (dbHandler.isUsernameExists(login_field.getText())) {
            messageLabel.setText("Username already exists!");
            return;
        }

        String name = signUpName.getText();
        String surname = signUpSurname.getText();
        String username = login_field.getText();
        String password = password_field.getText();
        String gender = "";
        if (signUpCheckBoxWoman.isSelected())
            gender = "Female";
        else
            gender = "Male";
        User user = new User(name, surname, username, password, gender);
        dbHandler.signUpUser(user);
        String userId = dbHandler.getUserId(user.getUsername());
        user.setUserId(userId);
        messageLabel.setText("Account registered successfully!");
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> openNewScene("/task/finance/signIn.fxml"));
        delay.play();
    }

    public void openNewScene(String window) {
        Stage currentStage = (Stage) signUpButton.getScene().getWindow();
        currentStage.hide();
        signUpButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            currentStage.setOnHidden(event -> stage.show());

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}