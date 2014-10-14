import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by arseny on 14.10.14.
 */
public class TCPServer {

    private final static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static String process(String input) throws InterruptedException {
        Thread.sleep(5000);
        return input + "!";
    }

    public static void main(String[] args) throws Exception {
        int port = 50000;
        final ServerSocket socket = new ServerSocket(port);

        while (true) {

            final Socket connectionSocket = socket.accept();

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream is = connectionSocket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        OutputStream os = connectionSocket.getOutputStream();
                        Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                        final String line = reader.readLine();
                        String result = process(line);
                        writer.write(result + "\n");
                        writer.flush();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            });
        }
    }
}
