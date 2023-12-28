package task.finance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinancialReportController {

    @FXML
    private TreeView<String> financeTreeView;

    @FXML
    private Label selectedLabel;
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        loadDataFromDatabase();
    }
    private String getCurrentUserId() {
        return databaseHandler.getUserId(CurrentUserManager.getCurrentUsername());
    }
    private void addToSummaryMap(Map<String, Map<String, Map<String, Map<String, Double>>>> summaryMap,
                                 String entryType, String category, String subcategory, String date, Double amount) {
        category = category != null ? category : "Uncategorized";
        subcategory = subcategory != null ? subcategory : "Uncategorized";

        summaryMap
                .computeIfAbsent(entryType, k -> new HashMap<>())
                .computeIfAbsent(category, k -> new HashMap<>())
                .computeIfAbsent(subcategory, k -> new HashMap<>())
                .merge(date, amount, Double::sum);
    }

    private void loadDataFromDatabase() {
        String userId = getCurrentUserId();
        List<FinancialEntry> financialEntries = databaseHandler.fetchFinancialEntries(userId);

        Map<String, Map<String, Map<String, Map<String, Double>>>> summaryMap = new HashMap<>();

        for (FinancialEntry entry : financialEntries) {
            String entryType = entry.getEntryType().toString();

            for (Category category : entry.getCategories()) {
                String categoryName = category.getName();

                for (Subcategory subcategory : entry.getSubcategories()) {
                    String subcategoryName = (subcategory != null) ? subcategory.getName() : null;
                    String date = entry.getDate().toString();
                    Double amount = entry.getAmount();

                    addToSummaryMap(summaryMap, entryType, categoryName, subcategoryName, date, amount);
                }
            }
        }

        TreeItem<String> rootItem = new TreeItem<>("Financial Report");

        for (Map.Entry<String, Map<String, Map<String, Map<String, Double>>>> entryTypeEntry : summaryMap.entrySet()) {
            TreeItem<String> entryTypeItem = new TreeItem<>(entryTypeEntry.getKey());

            for (Map.Entry<String, Map<String, Map<String, Double>>> categoryEntry : entryTypeEntry.getValue().entrySet()) {
                TreeItem<String> categoryItem = new TreeItem<>("Category: " + categoryEntry.getKey());

                for (Map.Entry<String, Map<String, Double>> subcategoryEntry : categoryEntry.getValue().entrySet()) {
                    TreeItem<String> subcategoryItem = new TreeItem<>("Subcategory: " + subcategoryEntry.getKey());

                    for (Map.Entry<String, Double> dateEntry : subcategoryEntry.getValue().entrySet()) {
                        TreeItem<String> dateItem = new TreeItem<>("Date: " + dateEntry.getKey() + " - Amount: " + dateEntry.getValue());
                        subcategoryItem.getChildren().add(dateItem);
                    }

                    categoryItem.getChildren().add(subcategoryItem);
                }

                entryTypeItem.getChildren().add(categoryItem);
            }

            rootItem.getChildren().add(entryTypeItem);
        }

        financeTreeView.setRoot(rootItem);
    }
    @FXML
    private Button goToAddTransaction;
    public void goToAddTransaction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/finance/app.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button goToFinanceReportButton;
    public void goToFinanceReport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/finance/finance_report.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button logOutButton;
    public void logOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/finance/signIn.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
