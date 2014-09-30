import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arseny on 30.09.14.
 */
public interface AdderInterface extends Remote {
    public int add(int a, int b) throws RemoteException;
}
