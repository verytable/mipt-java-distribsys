import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arseny on 30.09.14.
 */
public interface MemoInterface extends Remote {

    public void addNote(String note) throws RemoteException;

    public String getNotes() throws RemoteException;
}
