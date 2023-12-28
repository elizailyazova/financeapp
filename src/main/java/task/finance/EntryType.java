package task.finance;

import lombok.Getter;

public class EntryType {
    public static final EntryType INCOME = new EntryType(1, "Income");
    public static final EntryType EXPENSES = new EntryType(2, "Expenses");

    @Getter
    private final int id;

    @Getter
    private final String name;

    EntryType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EntryType valueOf(int entryTypeId) {
        if (entryTypeId == INCOME.getId()) {
            return INCOME;
        } else if (entryTypeId == EXPENSES.getId()) {
            return EXPENSES;
        } else {
            throw new IllegalArgumentException("Invalid entry type ID: " + entryTypeId);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
