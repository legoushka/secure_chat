import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class DbController {

    private String db_username = "postgres";
    private String db_password = "root";
    private String db_url = "jdbc:postgresql://localhost:5432/postgres";

    private final String selectQuery = "SELECT * FROM users WHERE user_phonenum = ?";
    private final String insertQuery = "INSERT INTO users (user_phonenum, user_name, user_surname, user_gender, user_icon_url, password_hash) VALUES (?,?,?,?,?,?)";

    private Connection connection;

    private Logger logger;

    public DbController() {
    }

    public DbController(String username, String password, String url) {
        this.db_username = username;
        this.db_password = password;
        this.db_url = url;
    }

    public DbController(Logger logger) {
        this.logger = logger;
    }

    public void openConnection() throws SQLException {
        this.connection = DriverManager.getConnection(db_url, db_username, db_password);
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    public boolean compareUserPasswords(String phonenum, String password) {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, phonenum);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return (Objects.equals(rs.getString("password_hash"), password));
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean isRegLoginFree(String phonenum) {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, phonenum);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                rs.close();
                return false;
            } else {
                rs.close();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public User getActualUserData(User user) {
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, user.getPhonenum());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt(1),
                        user.getPhonenum(),
                        rs.getString("user_name"),
                        rs.getString("user_surname"),
                        rs.getBoolean("user_gender"),
                        rs.getString("user_icon_url"),
                        user.getPublicPGPkey()
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public User registerNewUser(User user, String password) {

        try (PreparedStatement statement =
                     connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, user.getPhonenum());
            statement.setString(2, user.getName());
            statement.setString(3, user.getSurnname());
            statement.setBoolean(4, user.getGender());
            statement.setString(5, user.getIconURL());
            statement.setString(6, password);
            statement.executeUpdate();
            int id;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                return new User(id, user.getPhonenum(), user.getName(), user.getSurnname(), user.getGender(), user.getIconURL(), user.getPublicPGPkey());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
