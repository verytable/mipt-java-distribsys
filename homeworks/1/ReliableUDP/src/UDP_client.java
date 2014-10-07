import java.io.IOException;

/**
 * Created by arseny on 30.09.14.
 */
public class UDP_client {

    public static void main(String[] args) throws IOException {
        int port =  50001;

        byte[] buffer = new byte[1000];
        System.in.read(buffer);

        ReliablePacket reliablePacket = new ReliablePacket(buffer);

        ReliableSocket reliableSocket = new ReliableSocket(port);

        reliableSocket.send(reliablePacket);

        System.out.println("send successfully");
    }
}
