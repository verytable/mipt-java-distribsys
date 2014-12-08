package coordinator;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arseny on 16.11.14.
 */
public interface CoordinatorInterface extends Remote {

    public ViewInfo ping(int viewNum, String name) throws RemoteException;

    public String primary() throws RemoteException;
}
