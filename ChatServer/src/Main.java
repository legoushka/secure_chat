import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Logger logger = null;
        try {
            logger = new Logger("src/log.txt");
        } catch (IOException e) {
            System.out.println("Не удалось запустить логгер по указаному пути");
        }

        Server server = new Server(new ServerRepository(new DbController(logger), logger), logger);

        ConsoleIOThread consoleIOThread = new ConsoleIOThread(server);
        consoleIOThread.start();


        while (server.isProgramStarted()) {
            if (server.isServerStarted()) {
                server.acceptClientsConnections();
            }
        }

        if (logger != null){
            logger.close();
        }
    }
}
