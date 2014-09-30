import java.nio.ByteBuffer;

/**
 * Created by arseny on 23.09.14.
 */
public class ReliablePacket {
    /*
    * Приписывает заголовок: первый байт: 1 - подтверждение, 0 - пакет
    * далее 8 байт - id пакета
    * данные
    * */
    public byte[] buffer;

    public ReliablePacket(byte[] buffer) {
        ByteBuffer reliableBuffer = ByteBuffer.allocate(1 + buffer.length);
        reliableBuffer.put((byte) 1);
        reliableBuffer.put(buffer);

        this.buffer = reliableBuffer.array();
    }

}
