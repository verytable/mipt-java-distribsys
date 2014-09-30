import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by arseny on 23.09.14.
 */
public class ReliableSocket {

    DatagramSocket datagramSocket;

    public static final int MAX_PACKET_LENGTH = 1000;
    private static final int BUFFER_LENGTH = MAX_PACKET_LENGTH + 1 + 8;

    private boolean isConfirmation(DatagramPacket datagramPacket, byte[] idBytes) {
        if (datagramPacket.getLength() == 9) {
            byte[] bytes = datagramPacket.getData();
            if (bytes[0] == 1) {
                for (int i = 1; i <= 8; ++i) {
                    if (bytes[i] != idBytes[i]) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void send(ReliablePacket packet) throws IOException {
        System.out.println("Sending");
        byte[] buffer = new byte[BUFFER_LENGTH];
        buffer[0] = 1;
        Random r = new Random();
        long id = r.nextLong();
        byte[] idBytes = ByteBuffer.allocate(8).putLong(id).array();
        System.arraycopy(idBytes, 0, buffer, 0, 8);
        System.arraycopy(packet.buffer, 0, buffer, 9, MAX_PACKET_LENGTH);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, BUFFER_LENGTH);
        while (true) {
            datagramSocket.send(datagramPacket);
            while (true) {
                DatagramPacket reply = new DatagramPacket(newbuffer, 9);
                datagramSocket.receive(reply);
                //check validity
                //if ok
                return;
                //if timeout ex
                break;
            }
        }
    }

    public ReliablePacket receive() throws IOException {
        System.out.println("Receiving");
        byte[] buffer = new byte[BUFFER_LENGTH];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, BUFFER_LENGTH);
        while (true) {
            datagramSocket.receive(datagramPacket);
            //check validity
            //if valid
            break;
        }
        buffer[0] = 1;
        DatagramPacket reply = new DatagramPacket(buffer, 0, 9);
        datagramSocket.send(reply);
        return new ReliablePacket(buffer); //buffer.drop(9)
    }
}
