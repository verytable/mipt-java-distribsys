import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by arseny on 14.10.14.
 */
public class TCPServer {
    public static String process(String input) throws InterruptedException {
        Thread.sleep(1000);
        return input + "!";
    }

    public static void main(String[] args) throws Exception {
        int port = 50000;
        ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connectionSocket = socket.accept();
            InputStream is = connectionSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            OutputStream os = connectionSocket.getOutputStream();
            Writer writer = new BufferedWriter(new OutputStreamWriter(os));
            String line = reader.readLine();
            String result = process(line);
            writer.write(result + "\n");
            writer.flush();
        }
    }
}
