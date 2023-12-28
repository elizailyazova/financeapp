package task.finance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class FinanceApp extends Application {
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("signIn.fxml"));
        primaryStage.setTitle("Finance App");
        primaryStage.setScene(new Scene(root, 915.71, 612.72));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

