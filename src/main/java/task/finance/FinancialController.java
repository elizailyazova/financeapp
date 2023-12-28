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

public class FinancialController {
    @FXML
    private ComboBox<EntryType> entryTypeComboBox;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<Subcategory> subcategoryComboBox;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private TextField amountTextField;

    @FXML
    private Button addEntryButton;

    private DatabaseHandler databaseHandler = new DatabaseHandler();

    public void initialize() {

        List<EntryType> entryTypes = databaseHandler.getEntryTypes();
        entryTypeComboBox.getItems().addAll(entryTypes);
        setupComboBox(entryTypeComboBox);

        entryTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<Category> categories = databaseHandler.getCategoriesByEntryType(newValue.getId());
                categoryComboBox.getItems().clear();
                categoryComboBox.getItems().addAll(categories);
                setupComboBox(categoryComboBox);
            }
        });

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<Subcategory> subcategories = databaseHandler.getSubcategoriesByCategory(newValue.getId());
                subcategoryComboBox.getItems().clear();
                subcategoryComboBox.getItems().addAll(subcategories);
                setupComboBox(subcategoryComboBox);
            }
        });
        addEntryButton.setOnAction(event -> addFinancialEntry());
    }


    public void addFinancialEntry() {
        try {
            String userId = getCurrentUserId();
            EntryType entryType = entryTypeComboBox.getValue();
            Category category = categoryComboBox.getValue();
            Subcategory subcategory = subcategoryComboBox.getValue();
            LocalDate date = dateDatePicker.getValue();
            double amount = Double.parseDouble(amountTextField.getText());

            FinancialEntry financialEntry = new FinancialEntry(0, userId, entryType, category, subcategory, date, amount);

            databaseHandler.addFinancialEntry(financialEntry);

            showAlert("Success", "Financial entry added successfully", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Error adding financial entry", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private <T> void setupComboBox(ComboBox<T> comboBox) {
        comboBox.setCellFactory(param -> new javafx.scene.control.ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.toString() == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        comboBox.setButtonCell(new javafx.scene.control.ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.toString() == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private String getCurrentUserId() {
        return databaseHandler.getUserId(CurrentUserManager.getCurrentUsername());
    }

    @FXML
    private TreeTableView<FinancialReportEntry> financialReportTreeTableView;

    @FXML
    private TreeTableColumn<FinancialReportEntry, String> entryTypeColumn;

    @FXML
    private TreeTableColumn<FinancialReportEntry, String> categoryColumn;

    @FXML
    private TreeTableColumn<FinancialReportEntry, String> subcategoryColumn;

    @FXML
    private TreeTableColumn<FinancialReportEntry, LocalDate> dateColumn;


    @FXML
    private TreeTableColumn<FinancialReportEntry, Double> amountColumn;

    @FXML
    private void viewFinanceReport() {
        try {
            financialReportTreeTableView.setRoot(buildFinancialReportTree());
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Error fetching financial report data", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private TreeItem<FinancialReportEntry> buildFinancialReportTree() throws SQLException, ClassNotFoundException {
        String userId = getCurrentUserId();
        List<FinancialEntry> financialEntries = databaseHandler.getFinancialEntriesByUserId(userId);

        TreeItem<FinancialReportEntry> root = new TreeItem<>(new FinancialReportEntry("Root", null, null, LocalDate.now(), 0.0));

        for (FinancialEntry entry : financialEntries) {
            String entryTypeName = entry.getEntryType().getName();
            System.out.println("Entry Type: " + entryTypeName);

            Category category = entry.getCategory();
            String categoryName = (category != null) ? category.getName() : null;
            System.out.println("Category: " + categoryName);

            Subcategory subcategory = entry.getSubcategory();
            String subcategoryName = (subcategory != null) ? subcategory.getName() : null;
            System.out.println("Subcategory: " + subcategoryName);

            TreeItem<FinancialReportEntry> entryTypeItem = findOrCreate(root, entryTypeName);
            TreeItem<FinancialReportEntry> categoryItem = findOrCreate(entryTypeItem, categoryName);

            String combinedKey = getCombinedKey(categoryName, subcategoryName);
            System.out.println("Combined Key: " + combinedKey);
            TreeItem<FinancialReportEntry> subcategoryItem = findOrCreate(categoryItem, combinedKey);

            subcategoryItem.getChildren().add(new TreeItem<>(new FinancialReportEntry(entryTypeName, categoryName, subcategoryName, entry.getDate(), entry.getAmount())));
        }

        return root;
    }

    private String getCombinedKey(String category, String subcategory) {
        return (subcategory != null) ? category + " - " + subcategory : category;
    }

    private TreeItem<FinancialReportEntry> findOrCreate(TreeItem<FinancialReportEntry> parent, String name) {
        for (TreeItem<FinancialReportEntry> child : parent.getChildren()) {
            if (child.getValue().getDescription().equals(name)) {
                return child;
            }
        }

        TreeItem<FinancialReportEntry> newItem = new TreeItem<>(new FinancialReportEntry(name, LocalDate.now(), 0.0));
        parent.getChildren().add(newItem);
        return newItem;
    }

    @FXML
    private Button goToMainButton;
    public void goToMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/task/finance/MainView.fxml"));
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

    public TreeTableColumn<FinancialReportEntry, String> getEntryTypeColumn() {
        return entryTypeColumn;
    }

    public void setEntryTypeColumn(TreeTableColumn<FinancialReportEntry, String> entryTypeColumn) {
        this.entryTypeColumn = entryTypeColumn;
    }

    public TreeTableColumn<FinancialReportEntry, String> getCategoryColumn() {
        return categoryColumn;
    }

    public void setCategoryColumn(TreeTableColumn<FinancialReportEntry, String> categoryColumn) {
        this.categoryColumn = categoryColumn;
    }

    public TreeTableColumn<FinancialReportEntry, String> getSubcategoryColumn() {
        return subcategoryColumn;
    }

    public void setSubcategoryColumn(TreeTableColumn<FinancialReportEntry, String> subcategoryColumn) {
        this.subcategoryColumn = subcategoryColumn;
    }

    public TreeTableColumn<FinancialReportEntry, Double> getAmountColumn() {
        return amountColumn;
    }

    public void setAmountColumn(TreeTableColumn<FinancialReportEntry, Double> amountColumn) {
        this.amountColumn = amountColumn;
    }

    public TreeTableColumn<FinancialReportEntry, LocalDate> getDateColumn() {
        return dateColumn;
    }

    public void setDateColumn(TreeTableColumn<FinancialReportEntry, LocalDate> dateColumn) {
        this.dateColumn = dateColumn;
    }

    public Button getGoToFinanceReportButton() {
        return goToFinanceReportButton;
    }

    public void setGoToFinanceReportButton(Button goToFinanceReportButton) {
        this.goToFinanceReportButton = goToFinanceReportButton;
    }

}


