import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server {
    private ServerSocket serverSocket;
    private static volatile boolean isServerStarted = false;
    private static volatile boolean isProgramStarted = true;
    private final ServerRepository repository;
    private final Logger logger;


    public Server(ServerRepository repository, Logger logger) {
        this.logger = logger;
        this.repository = repository;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isServerStarted() {
        return isServerStarted;
    }

    public boolean isProgramStarted() {
        return isProgramStarted;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        repository.openDB();
        isServerStarted = true;
        logger.sendMessageToLog("Сервер запущен на порту: " + port);
    }

    public void stop() {
        try {
            logger.sendMessageToLog("Сервер успешно остановлен");
            repository.closeDB();

            if (serverSocket != null && !serverSocket.isClosed()) {
                for (Map.Entry<User, SocketConnection> user : repository.getUsersConnections().entrySet()) {
                    user.getValue().close();
                }
                serverSocket.close();
                repository.getUsersConnections().clear();
            }

        } catch (Exception e) {
            logger.sendMessageToLog("Ошибка при остановке сервера");
        }
        isServerStarted = false;
    }

    public void stopProgram() {
        isProgramStarted = false;
    }

    public void acceptClientsConnections() {
        while (isServerStarted) {
            try {
                Socket socket = serverSocket.accept();
                logger.sendMessageToLog("Подключение получено: " + socket.getInetAddress());
                new ServerThread(socket, repository, this, logger).start();
            } catch (Exception e) {
                logger.sendMessageToLog("Ошибка при получени подключения");
                stop();
                break;
            }
        }
    }

    public void sendMessageToClients(Message message) {
        for (Map.Entry<User, SocketConnection> user : repository.getUsersConnections().entrySet()) {
            try {
                user.getValue().send(message);
            } catch (Exception e) {
                logger.sendMessageToLog("Ошибка рассылки сообщения пользователям");
            }
        }
    }
}
