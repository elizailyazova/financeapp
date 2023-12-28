package task.finance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.finance.animations.Shake;
import javafx.scene.control.Label;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class signInController {

    @FXML
    private Button authSigninButton;

    @FXML
    private Button loginSignupButton;

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;
    @FXML
    private Label messageLabel;

    @FXML
    void initialize() {
        authSigninButton.setOnAction(event -> {
            String loginText = login_field.getText().trim();
            String loginPassword = password_field.getText().trim();

            if(!loginText.equals("") && !loginPassword.equals("")) {
                loginUser(loginText, loginPassword);
            }
            else
                messageLabel.setText("Username or password cannot be empty");
        });
        loginSignupButton.setOnAction(event -> {
            openNewScene("/task/finance/signUp.fxml");
        });
    }

    private void loginUser(String loginText, String loginPassword) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User();
        user.setUsername(loginText);
        user.setPassword(loginPassword);
        ResultSet result = dbHandler.getUser(user);
        int counter = 0;
        try {
            while (result.next()) {
                counter++;
            }
            if (counter >= 1) {
                CurrentUserManager.setCurrentUsername(loginText);
                openNewScene("/task/finance/app.fxml");
            } else {
                messageLabel.setText("The username or password is incorrect");

                Shake userLoginAnim = new Shake(login_field);
                Shake userPassAnim = new Shake(password_field);
                userLoginAnim.playAnim();
                userPassAnim.playAnim();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void openNewScene(String window) {
        loginSignupButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

}
