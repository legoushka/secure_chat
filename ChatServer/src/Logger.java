import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger implements Closeable {
    private final FileWriter fw;

    public Logger(String filepath) throws IOException {
        File file = new File(filepath);
        fw = new FileWriter(file, true);
    }



    public void sendMessageToLog(String message) {
        synchronized (fw){
            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            String formattedDate = "[" + formatForDateNow.format(dateNow) + "] ";
            try {
                fw.append((formattedDate +": " + message + '\n'));
            } catch (IOException e){
                System.out.println("Не удалось записать сообщение в логи");
            }
        }
    }

    @Override
    public void close() {
        try {
            fw.close();
        }catch (IOException e){
            System.out.println("Не удалось остановить логгер");
        }
    }
}
