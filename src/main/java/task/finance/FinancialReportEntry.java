package task.finance;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class FinancialReportEntry {
    @Setter
    private String entryType;

    @Setter
    private String category;

    @Setter
    private String subcategory;

    @Setter
    private LocalDate date;

    @Setter
    private double amount;

    public FinancialReportEntry(String entryType, String category, String subcategory, LocalDate date, double amount) {
        this.entryType = entryType;
        this.category = category;
        this.subcategory = subcategory;
        this.date = date;
        this.amount = amount;
    }
    public FinancialReportEntry(String entryType, LocalDate date, double amount) {
        this.entryType = entryType;
        this.date = date;
        this.amount = amount;
    }

    public String getDescription() {
        return subcategory != null ? subcategory : (category != null ? category : entryType);
    }
}
