import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by arseny on 14.10.14.
 */
public class TCPClient {
    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        int port = 50000;
        Socket socket = new Socket(address, port);
        InputStream is = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        OutputStream os = socket.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(os));

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String line = input.readLine();

        writer.write(line + "\n");
        writer.flush();

        String answer  = reader.readLine();
        System.out.println(answer);
    }
}
