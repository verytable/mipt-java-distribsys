import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by arseny on 30.09.14.
 */
public class ReliableSocket {

    DatagramSocket datagramSocket;

    public static final int MAX_PACKET_LENGTH = 1000;
    private static final int BUFFER_LENGTH = MAX_PACKET_LENGTH + 1 + 8;
    public static final int CONFIRMATION_PACKET_LENGTH = 9;
    public static final int RECEIVE_TIMEOUT = 10000;

    public ReliableSocket(int port) throws SocketException {
        datagramSocket = new DatagramSocket(port);
        datagramSocket.setSoTimeout(RECEIVE_TIMEOUT);
    }

    private boolean isConfirmation(DatagramPacket datagramPacket, byte[] idBytes) {
        byte[] bytes = datagramPacket.getData();
        if (datagramPacket.getLength() == CONFIRMATION_PACKET_LENGTH && bytes[0] == 1) {
            for (int i = 1; i <= 8; ++i) {
                if (bytes[i] != idBytes[i - 1]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isPacket(DatagramPacket datagramPacket) {
        byte[] bytes = datagramPacket.getData();
        return bytes.length == BUFFER_LENGTH && bytes[0] == 0;
    }

    public void send(ReliablePacket packet) throws IOException {
        System.out.println("Sending");
        byte[] buffer = new byte[BUFFER_LENGTH];

        buffer[0] = 0;

        Random r = new Random();
        long id = r.nextLong();
        byte[] idBytes = ByteBuffer.allocate(8).putLong(id).array();
        System.arraycopy(idBytes, 0, buffer, 1, 8);

        System.arraycopy(packet.buffer, 0, buffer, 9, MAX_PACKET_LENGTH);

        DatagramPacket datagramPacket =
            new DatagramPacket(buffer, 0, BUFFER_LENGTH,
                               InetAddress.getLocalHost(), 50000);

        while (true) {
            datagramSocket.send(datagramPacket);
            while (true) {
                System.out.print("Waiting for the confirmation\n");
                byte[] replyBuffer = new byte[CONFIRMATION_PACKET_LENGTH];
                DatagramPacket reply =
                    new DatagramPacket(replyBuffer, CONFIRMATION_PACKET_LENGTH);
                try {
                    datagramSocket.receive(reply);
                    if (isConfirmation(reply, idBytes)) {
                        return;
                    }
                } catch (SocketTimeoutException ex) {
                    System.out.println("Timeout reached: " + ex.getMessage());
                }
                break;
            }
        }
    }

    public ReliablePacket receive() throws IOException {
        System.out.println("Receiving");
        byte[] buffer = new byte[BUFFER_LENGTH];
        DatagramPacket datagramPacket =
            new DatagramPacket(buffer, BUFFER_LENGTH);

        while (true) {
            try {
                datagramSocket.receive(datagramPacket);
                if (isPacket(datagramPacket)) {
                    break;
                }
            } catch (SocketTimeoutException ignored) {
            }
        }

        buffer[0] = 1;
        DatagramPacket reply =
            new DatagramPacket(buffer, 0, CONFIRMATION_PACKET_LENGTH,
                               InetAddress.getLocalHost(),
                               datagramPacket.getPort());

        datagramSocket.send(reply);

        byte[] data = new byte[datagramPacket.getLength()];
        System.arraycopy(buffer, 9, data, 0, datagramPacket.getLength() - 9);

        return new ReliablePacket(data);
    }
}
