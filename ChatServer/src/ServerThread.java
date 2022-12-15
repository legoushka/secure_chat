import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerThread extends Thread {
    private final Socket socket;
    private final ServerRepository repository;
    private final Server server;

    private final Logger logger;

    public ServerThread(Socket socket, ServerRepository repository, Server server, Logger logger) {
        this.socket = socket;
        this.repository = repository;
        this.server = server;
        this.logger = logger;
    }

    private User processRequests(SocketConnection connection) {
        try {
            connection.send(new Message(MessageType.LOGIN_REQUEST));
            logger.sendMessageToLog("Запрос входа отправлен: " + connection.getSocket().getInetAddress());
            Message responseMessage = connection.receive();
            logger.sendMessageToLog("Ответ получен от: " + connection.getSocket().getInetAddress());
            if (responseMessage.getMessageType() == MessageType.USER_DATA) {
                logger.sendMessageToLog("Запрос входа от: " + connection.getSocket().getInetAddress());
                return processLoginRequest(responseMessage, connection);
            } else if (responseMessage.getMessageType() == MessageType.REGISTRATION_REQUEST) {
                logger.sendMessageToLog("Запрос регистрации от: " + connection.getSocket().getInetAddress());
                return processRegisterRequest(responseMessage, connection);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.sendMessageToLog("Ошибка при обработке запроса от пользователя: " + connection.getSocket().getInetAddress());
        }
        return null;
    }

    private User processLoginRequest(Message responseMessage, SocketConnection connection) throws IOException {
        if (repository.isLoginDataValid(responseMessage)
                && !repository.getUsersConnections().containsKey(responseMessage.getUser())) {
            User user = repository.getActualUserData(responseMessage.getUser());
            repository.addUserToMap(user, connection);
            Set<User> userList = new HashSet<>();
            for (Map.Entry<User, SocketConnection> users : repository.getUsersConnections().entrySet()) {
                userList.add(users.getKey());
            }
            connection.send(new Message(MessageType.LOGIN_ACCEPTED, null, user, userList.stream().toList()));
            logger.sendMessageToLog("Пользователь " + connection.getSocket().getInetAddress() +" " +user.getId() + " " + user.getPhonenum() + " успешно подключился ");
            server.sendMessageToClients(new Message(MessageType.USER_JOINED, null, user, null));
            return user;
        } else {
            logger.sendMessageToLog("Запрос пользователя на вход отклонен: " + connection.getSocket().getInetAddress());
            connection.send(new Message(MessageType.LOGIN_REJECTED));
            return null;
        }
    }

    private User processRegisterRequest(Message responseMessage, SocketConnection connection) throws IOException {
        if (repository.isRegLoginFree(responseMessage)) {
            User user = repository.registerUser(responseMessage);
            connection.send(new Message(MessageType.REGISTER_ACCEPTED));
            logger.sendMessageToLog("Пользователь " + connection.getSocket().getInetAddress() + " " + user.getId() + " " + user.getPhonenum() + " зарегистрировался");
            return null;
        } else {
            logger.sendMessageToLog("Регистрация пользователя отклонена: " + connection.getSocket().getInetAddress());
            connection.send(new Message(MessageType.REGISTER_REJECTED));
            return null;
        }
    }
    private void processMessagesForwarding(SocketConnection connection, User user) throws IOException {
        while (true) {
            try {
                Message message = connection.receive();
                if (message.getMessageType() == MessageType.TEXT_MESSAGE) {
                    server.sendMessageToClients(new Message(MessageType.TEXT_MESSAGE, message.getContent(), message.getUser(), null));
                }
                if (message.getMessageType() == MessageType.USER_LEFT) {
                    server.sendMessageToClients(new Message(MessageType.USER_LEFT, null, message.getUser(), null));
                    logger.sendMessageToLog("Пользователь покинул чат: " + message.getUser().getId() + " " + message.getUser().getPhonenum() + " " + connection.getSocket().getInetAddress());
                    repository.removeUserFromMap(message.getUser());
                    connection.close();
                    break;
                }
            } catch (Exception e) {
                logger.sendMessageToLog("Ошибка при рассылке сообщения пользователям");
                server.sendMessageToClients(new Message(MessageType.USER_LEFT, null, user, null));
                repository.removeUserFromMap(user);
                connection.close();
                break;
            }
        }
    }
    @Override
    public void run() {
        try {
            SocketConnection connection = new SocketConnection(socket);
            User user = processRequests(connection);
            if (user != null){
                processMessagesForwarding(connection, user);
            }
        } catch (Exception e) {
            logger.sendMessageToLog("Ошибка при обработке потока сервера");
        }
    }
}