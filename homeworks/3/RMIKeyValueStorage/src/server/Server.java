package server;

import coordinator.CoordinatorInterface;
import coordinator.ViewInfo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arseny on 08.12.14.
 */
public class Server extends UnicastRemoteObject implements ServerInterface {

    private CoordinatorInterface coordinator;
    private ServerInterface backup;
    private String server;
    private ViewInfo viewInfo;
    private HashMap<String, String> data;

    public Server(String serverName, String coordinatorName) throws RemoteException {
        viewInfo = new ViewInfo();
        try {
            coordinator = (CoordinatorInterface) Naming.lookup(coordinatorName);
        } catch (NotBoundException | MalformedURLException ex) {
            System.err.println(ex.getMessage());
        }
        backup = null;
        server = serverName;
        data = new HashMap<>();
    }

    @Override
    public void put(String key, String value) throws RemoteException {
        // must be invoked by primary only
        if (viewInfo.primary.equals(server)) {
            if (!viewInfo.backup.isEmpty()) {
                if (backup != null) {
                    backup.putBackup(key, value);
                }
            }
            data.put(key, value);
        } else {
            throw new IncorrectOperationException("put request invoked by not" +
                                                  "a primary");
        }
    }

    @Override
    public void putBackup(String key, String value) throws RemoteException {
        if (!viewInfo.backup.isEmpty() && viewInfo.backup.equals(server)) {
            data.put(key, value);
        } else {
            throw new IncorrectOperationException("putBackup request invoked" +
                                                  " not in backup");
        }
    }

    @Override
    public String get(String key) throws RemoteException {
        // can be invoked by primary only
        if (viewInfo.primary.equals(server)) {
            return data.get(key);
        } else {
            throw new IncorrectOperationException("get request invoked by" +
                                                  " not a primary");
        }
    }

    public void tick() throws RemoteException {
        ViewInfo newViewInfo = coordinator.ping(viewInfo.view, server);
        if (!newViewInfo.backup.equals(viewInfo.backup) && !newViewInfo.backup.isEmpty()) {
            try {
                backup = (ServerInterface) Naming.lookup(newViewInfo.backup);
            } catch (NotBoundException | MalformedURLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        if (!newViewInfo.backup.equals(viewInfo.backup) &&
            !newViewInfo.backup.isEmpty() &&
            viewInfo.primary.equals(server)) {

            viewInfo = newViewInfo;
            try {
                ServerInterface backup =
                        (ServerInterface) Naming.lookup(viewInfo.backup);
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    backup.putBackup(key, value);
                }
                viewInfo = coordinator.ping(viewInfo.view, server);
            } catch (NotBoundException | MalformedURLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        viewInfo = newViewInfo;
    }
}
