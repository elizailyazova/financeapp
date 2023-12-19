package task.finance;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MainController {

    @FXML
    private void openCategoryManagement() {
        // Логика открытия окна управления категориями
        showAlert("Управление категориями", "Открывается управление категориями.");
    }

    @FXML
    private void openBudgetPlanning() {
        // Логика открытия окна планирования бюджета
        showAlert("Планирование бюджета", "Открывается планирование бюджета.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
