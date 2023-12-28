package task.finance;

import javafx.scene.control.TreeItem;

import java.time.LocalDate;

public class FinancialTreeItem {
    private String entryType;
    private String category;
    private String subcategory;
    private LocalDate date;
    private double amount;

    public TreeItem<String> toTreeItem() {
        TreeItem<String> entryTypeItem = new TreeItem<>(entryType);
        TreeItem<String> categoryItem = new TreeItem<>("Category: " + category);
        TreeItem<String> subcategoryItem = new TreeItem<>("Subcategory: " + subcategory);
        TreeItem<String> dateItem = new TreeItem<>("Date: " + date);
        TreeItem<String> amountItem = new TreeItem<>("Amount: " + amount);

        entryTypeItem.getChildren().add(categoryItem);
        categoryItem.getChildren().add(subcategoryItem);
        subcategoryItem.getChildren().addAll(dateItem, amountItem);

        return entryTypeItem;
    }


    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
