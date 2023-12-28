package task.finance;

import lombok.Getter;
import lombok.Setter;

public class Subcategory {
    @Getter @Setter
    private int id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private int categoryId;

    public Subcategory(int id, String name, int categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }
    public Subcategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Subcategory valueOf(String subcategory) {
        return Subcategory.valueOf(subcategory);
    }

    @Override
    public String toString() {
        return name;
    }

}
