package task.finance;
import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter @Setter
    private String userId;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String surname;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String gender;

    public User(String userId, String name, String surname, String username, String password, String gender) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.gender = gender;
    }
    public User(String name, String surname, String username, String password, String gender) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.gender = gender;
    }
    public User() {}
}
