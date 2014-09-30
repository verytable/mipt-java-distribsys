import java.io.IOException;

/**
 * Created by arseny on 30.09.14.
 */
public class UDP_server {

    public static void main(String[] args) throws IOException {
        int port =  50000;

        ReliablePacket packet;
        ReliableSocket socket = new ReliableSocket(port);

        packet = socket.receive();

        String result = new String(packet.buffer);

        System.out.println("received: " + result);
    }
}
