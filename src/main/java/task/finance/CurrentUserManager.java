package task.finance;

public class CurrentUserManager {
    private static String currentUsername;

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
}



