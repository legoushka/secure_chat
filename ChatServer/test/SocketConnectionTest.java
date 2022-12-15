import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketConnectionTest {

    @Test
    void nullSocketConnectionConstructor() {
        Socket socket = null;
        assertThrows(NullPointerException.class,
                () ->
                {
                    SocketConnection socketConnection = new SocketConnection(socket);
                });
    }

    @Test
    void socketConnectionSendAndReceiveMessage() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8080);
                SocketConnection socketConnection = new SocketConnection(serverSocket.accept());
                socketConnection.send(new Message(MessageType.TEXT_MESSAGE, "TEST", null, null));
            } catch (IOException e) {
                //TODO
            }
        }).start();

        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8080);
                SocketConnection socketConnection = new SocketConnection(socket);
                Message message = socketConnection.receive();
                assertEquals(
                        "TEST",
                        message.getMessageContent()
                );
            } catch (Exception e) {
                //TODO
            }
        }).start();
    }

    @Test
    void closeNotNullSocketConnection() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8080);
                SocketConnection socketConnection = new SocketConnection(serverSocket.accept());
                assertDoesNotThrow(
                        () -> {
                            socketConnection.close();
                        }
                );
            } catch (IOException e) {
                //TODO
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(150);
                Socket socket = new Socket("localhost", 8080);
                SocketConnection socketConnection = new SocketConnection(socket);
                assertDoesNotThrow(
                        () -> {
                            socketConnection.close();
                        }
                );
            } catch (Exception e) {
                //TODO
            }
        }).start();

    }

}