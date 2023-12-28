package task.finance;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class FinancialEntry {
    @Getter @Setter
    private int id;
    @Getter @Setter
    private String userId;
    @Getter @Setter
    private EntryType entryType;
    @Getter @Setter
    private Category category;
    @Getter @Setter
    private Subcategory subcategory;
    @Getter @Setter
    private LocalDate date;
    @Getter @Setter
    private double amount;

    public FinancialEntry(int id, String userId, EntryType entryType, Category category, Subcategory subcategory, LocalDate date, double amount) {
        this.id = id;
        this.userId = userId;
        this.entryType = entryType;
        this.category = category;
        this.subcategory = subcategory;
        this.date = date;
        this.amount = amount;
    }
    private List<Category> categories;
    private List<Subcategory> subcategories;

    public FinancialEntry(int id, String userId, EntryType entryType, List<Category> categories, List<Subcategory> subcategories, LocalDate date, double amount) {
        this.id = id;
        this.userId = userId;
        this.entryType = entryType;
        this.categories = categories;
        this.subcategories = subcategories;
        this.date = date;
        this.amount = amount;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }


    public EntryType getEntryType() {
        return entryType;
    }

    public Category getCategory() {
        return category;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
    private int categoryId;
    private Integer subcategoryId;  // Use Integer to allow null

    // Constructor...

    public int getCategoryId() {
        return category != null ? category.getId() : 0;
    }

    public Integer getSubcategoryId() {
        return subcategory != null ? subcategory.getId() : null;
    }

}
