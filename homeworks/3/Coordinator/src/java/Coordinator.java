package java;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by arseny on 16.11.14.
 */
public class Coordinator extends UnicastRemoteObject implements CoordinatorInterface {

    public static int deadPings = 3;

    public Coordinator() throws RemoteException {
        currentTime = 0;
        // your initialization here
    }

    // this method is to be called by server
    public ViewInfo ping(int view, String serverName) throws RemoteException {
        // your code here
    }

    // this method is to be called by client
    public String primary() throws RemoteException {
        // your code here
    }

    // this method is to be called automatically as time goes by
    public void tick() {
        ++currentTime;
        // your code here
    }

    private long currentTime;

    // your private members here
}
