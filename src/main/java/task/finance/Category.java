package task.finance;

import lombok.Getter;
import lombok.Setter;

public class Category {
    @Getter @Setter
    private int id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private int typeId;

    public Category(int id, String name, int typeId) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
    }
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Category valueOf(String category) {
        return Category.valueOf(category);
    }

    @Override
    public String toString() {
        return name;
    }
}
