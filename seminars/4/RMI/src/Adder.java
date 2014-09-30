import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by arseny on 30.09.14.
 */
class Adder extends UnicastRemoteObject implements AdderInterface {

    public Adder() throws RemoteException {}

    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
