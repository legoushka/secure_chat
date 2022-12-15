import java.util.Scanner;

public class ConsoleIOThread extends Thread{

    private final Server server;

    public ConsoleIOThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        int option = -1;
        Scanner in = new Scanner(System.in);
        while (true){
            System.out.println("Выберите опцию: \n1 - Запустить сервер \n2 - Остановить сервер\n0 - Выход из программы");
            option = in.nextInt();
            if(option == 1){
                if(!server.isServerStarted()){
                    int port;
                    System.out.println("Введите порт: ");
                    port = in.nextInt();
                    try{
                        server.start(port);
                        System.out.println("Сервер успешно запущен");
                    } catch (Exception e){
                        System.out.println("Не удалось запустить сервер, попробуйте еще раз");
                    }
                }
                else{
                    System.out.println("Сервер уже запущен");
                }
            }
            if(option == 2){
                server.stop();
                System.out.println("Сервер остановлен");
            }
            if (option == 0){
                if(server.isServerStarted()){
                    System.out.println("Сначала остановите сервер!");
                }
                if (!server.isServerStarted()){
                    in.close();
                    server.stopProgram();
                    break;
                }
            }
        }
    }
}
