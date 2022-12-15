import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServerRepository {
    private DbController db;
    private Logger logger;
    private Map<User, SocketConnection> usersConnections = new HashMap<>();
    public Map<User, SocketConnection> getUsersConnections() {
        return usersConnections;
    }

    public ServerRepository(DbController db, Logger logger) {
        this.logger = logger;
        this.db = db;
    }
    public void openDB() {
        try {
            db.openConnection();
            logger.sendMessageToLog("Успешное подключение к базе данных");
            //System.out.println("Успешное подключение к базе данных");
        } catch (SQLException e) {
            logger.sendMessageToLog("Ошибка при подключении к базе данных :" + e.getSQLState() + e.getMessage());
        }
    }

    public void closeDB() {
        try {
            db.closeConnection();
            logger.sendMessageToLog("Успешное отключение от базы данных");
            //System.out.println("Успешное отключение от базы данных");
        } catch (SQLException e) {
            logger.sendMessageToLog("Ошибка при отключении от базы данных :" + e.getSQLState() + e.getMessage());
        }

    }

    public boolean isLoginDataValid(Message responseMessage) {
        return db.compareUserPasswords(responseMessage.getUser().getPhonenum(), responseMessage.getContent());
    }

    public boolean isRegLoginFree(Message responseMessage) {
        return db.isRegLoginFree(responseMessage.getUser().getPhonenum());
    }

    public User getActualUserData(User user) {
        return db.getActualUserData(user);
    }


    public User registerUser(Message responseMessage) {
        return db.registerNewUser(responseMessage.getUser(), responseMessage.getContent());
    }

    public void addUserToMap(User user, SocketConnection connection){
        usersConnections.put(user, connection);
    }

    public void removeUserFromMap(User user){
        usersConnections.remove(user);
    }
}
