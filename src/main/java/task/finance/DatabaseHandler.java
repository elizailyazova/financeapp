package task.finance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHandler extends Configs{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void signUpUser(User user) throws SQLException {
        String insert = "INSERT INTO " + Const.USER_TABLE + "(" + Const.USERS_NAME + "," + Const.USERS_SURNAME +
                "," + Const.USERS_USERNAME + "," + Const.USERS_PASSWORD + "," + Const.USERS_GENDER + ")" +
                "VALUES(?,?,?,?,?)";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, user.getName());
            prSt.setString(2, user.getSurname());
            prSt.setString(3, user.getUsername());
            prSt.setString(4, user.getPassword());
            prSt.setString(5, user.getGender());
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getUser(User user) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                Const.USERS_USERNAME  + "=? AND " + Const.USERS_PASSWORD + "=?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getUsername());
            prSt.setString(2, user.getPassword());
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }
    public boolean isUsernameExists(String username) throws SQLException, ClassNotFoundException {
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USERS_USERNAME + "=?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, username);
            ResultSet resultSet = prSt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String getUserId(String username) {
        String userId = null;
        String select = "SELECT " + Const.USERS_ID + " FROM " + Const.USER_TABLE + " WHERE " + Const.USERS_USERNAME + "=?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, username);
            ResultSet resultSet = prSt.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getString(Const.USERS_ID);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return userId;
    }

    public List<EntryType> getEntryTypes() {
        List<EntryType> entryTypes = new ArrayList<>();
        String select = "SELECT * FROM entry_type";
        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(select);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                entryTypes.add(new EntryType(id, name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return entryTypes;
    }

    public List<Category> getCategoriesByEntryType(int entryTypeId) {
        List<Category> categories = new ArrayList<>();
        String select = "SELECT * FROM category WHERE type_id = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(select)) {

            preparedStatement.setInt(1, entryTypeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int typeId = resultSet.getInt("type_id");
                    categories.add(new Category(id, name, typeId));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Subcategory> getSubcategoriesByCategory(int categoryId) {
        List<Subcategory> subcategories = new ArrayList<>();
        String select = "SELECT * FROM subcategory WHERE category_id = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(select)) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    subcategories.add(new Subcategory(id, name));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return subcategories;
    }
    public void addFinancialEntry(FinancialEntry financialEntry) throws SQLException, ClassNotFoundException {
        String insert = "INSERT INTO " + Const.FINANCIAL_ENTRY_TABLE + "(" +
                Const.FINANCIAL_ENTRY_USER_ID + "," +
                Const.FINANCIAL_ENTRY_TYPE + "," +
                Const.FINANCIAL_ENTRY_CATEGORY + "," +
                Const.FINANCIAL_ENTRY_SUBCATEGORY + "," +
                Const.FINANCIAL_ENTRY_DATE + "," +
                Const.FINANCIAL_ENTRY_AMOUNT + ")" +
                "VALUES(?,?,?,?,?,?)";


        try {
            Connection connection = getDbConnection();
            PreparedStatement prSt = connection.prepareStatement(insert);

            prSt.setString(1, financialEntry.getUserId());
            prSt.setInt(2, financialEntry.getEntryType().getId());
            prSt.setInt(3, financialEntry.getCategoryId());
            if (financialEntry.getSubcategoryId() != null) {
                prSt.setInt(4, financialEntry.getSubcategoryId());
            } else {
                prSt.setNull(4, Types.INTEGER);
            }
            prSt.setDate(5, Date.valueOf(financialEntry.getDate()));
            prSt.setDouble(6, financialEntry.getAmount());

            prSt.executeUpdate();

            // Close resources
            prSt.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<FinancialEntry> fetchFinancialEntries(String userId) {
        List<FinancialEntry> entries = new ArrayList<>();

        String select = "SELECT * FROM financial_entry WHERE user_id=?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, userId);
            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                EntryType entryType = EntryType.valueOf(resultSet.getInt("type_id"));

                List<Category> categories = getCategoriesByEntryType(entryType.getId());

                List<Subcategory> subcategories = getSubcategoriesByCategory(resultSet.getInt("category_id"));

                LocalDate date = resultSet.getDate("date").toLocalDate();
                double amount = resultSet.getDouble("amount");

                FinancialEntry entry = new FinancialEntry(id, userId, entryType, categories, subcategories, date, amount);
                entries.add(entry);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }
    public Map<String, Double> calculateTotalAmountByCategory(String userId) throws SQLException, ClassNotFoundException {
        Map<String, Double> totalAmounts = new HashMap<>();
        String query = "SELECT category.name, SUM(financial_entry.amount) as totalAmount " +
                "FROM financial_entry " +
                "JOIN category ON financial_entry.category_id = category.id " +
                "WHERE financial_entry.user_id = ? " +
                "GROUP BY category.name";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryName = resultSet.getString("name");
                    double totalAmount = resultSet.getDouble("totalAmount");
                    totalAmounts.put(categoryName, totalAmount);
                }
            }
        }

        return totalAmounts;
    }


    public List<FinancialEntry> getFinancialEntriesByUserId(String userId) throws SQLException, ClassNotFoundException {
        List<FinancialEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM financial_entry WHERE user_id = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    EntryType entryType = getEntryTypeById(resultSet.getInt("type_id"));
                    Category category = getCategoryById(resultSet.getInt("category_id"));
                    Subcategory subcategory = getSubcategoryById(resultSet.getInt("subcategory_id"));
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    double amount = resultSet.getDouble("amount");

                    // Fetch names based on IDs
                    String entryTypeName = getEntryTypeNameById(entryType.getId());
                    String categoryName = getCategoryNameById(category.getId());
                    String subcategoryName = getSubcategoryNameById(subcategory.getId());

                    FinancialEntry entry = new FinancialEntry(id, userId, entryType, category, subcategory, date, amount);
                    entries.add(entry);
                }
            }
        }

        return entries;
    }
    public String getEntryTypeNameById(int typeId) throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM entry_type WHERE id = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, typeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        }
        return null; // Handle the case when no EntryType with the given ID is found
    }

    public String getCategoryNameById(int categoryId) throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM category WHERE id = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        }
        return null; // Handle the case when no Category with the given ID is found
    }

    public String getSubcategoryNameById(int subcategoryId) throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM subcategory WHERE id = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, subcategoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        }
        return null;
    }

    private EntryType getEntryTypeById(int typeId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM entry_type WHERE id = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, typeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    return new EntryType(typeId, name);
                }
            }
        }

        return null;
    }

    public Category getCategoryById(int categoryId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM category WHERE id = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, categoryId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    return new Category(categoryId, name);
                }
            }
        }

        return null;
    }

    public Subcategory getSubcategoryById(int subcategoryId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM subcategory WHERE id = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, subcategoryId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    return new Subcategory(subcategoryId, name);
                }
            }
        }

        return null;
    }
}
