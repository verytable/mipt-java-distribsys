package coordinator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by arseny on 16.11.14.
 */
public class Coordinator extends UnicastRemoteObject implements CoordinatorInterface {

    public static int deadPings = 3;
    // your private members here
    private long currentTime;
    private ViewInfo viewInfo;
    private HashMap<String, Long> servers;
    private Boolean approved;

    public Coordinator() throws RemoteException {
        currentTime = 0;
        // your initialization here
        servers = new HashMap<String, Long>();
        approved = false;
        viewInfo = new ViewInfo();
    }

    // this method is to be called by server
    public ViewInfo ping(int view, String serverName) throws RemoteException {
        // update lastPingTime
        servers.put(serverName, currentTime);

        // primary restarted
        if (serverName.equals(viewInfo.primary) && view == 0 && approved) {
            // new primary is old backup
            viewInfo.primary = viewInfo.backup;
            updateBackup();
        }

        // primary is not set yet
        if (viewInfo.primary.equals("")) {
            viewInfo.primary = serverName;
            viewInfo.view += 1;
            approved = false;
        }

        //backup restart
        if (serverName.equals(viewInfo.backup) && view == 0 && approved) {
            viewInfo.view += 1;
            approved = false;
        }

        // backup is not set yet
        if (viewInfo.backup.isEmpty() && !viewInfo.primary.equals(serverName)) {
            viewInfo.backup = serverName;
            viewInfo.view += 1;
        }

        // assure coordinator that primary knows who he is
        if (serverName.equals(viewInfo.primary) && view == viewInfo.view) {
            approved = true;
        }

        if (!viewInfo.primary.equals(serverName) && viewInfo.backup.isEmpty() && approved) {
            updateBackup();
        }

        return new ViewInfo(viewInfo);
    }

    // this method is to be called by client
    public String primary() throws RemoteException {
        return viewInfo.primary;
    }

    // this method is to be called automatically as time goes by
    public void tick() {
        ++currentTime;

        // remove servers that are silent for deadPings pings
        Iterator<Map.Entry<String, Long>> it = servers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            String serverName = entry.getKey();
            Long lastPingTime = entry.getValue();
            if (lastPingTime + deadPings < currentTime) {
                it.remove();
            }
        }
        ;

        // primary removed due to deadPing
        if (!servers.containsKey(viewInfo.primary) && approved) {
            // new primary is old backup
            viewInfo.primary = viewInfo.backup;
            updateBackup();
        } else {
            // backup removed due to deadPing
            if (!viewInfo.backup.isEmpty() && !servers.containsKey(viewInfo.backup) && approved) {
                updateBackup();
            }
        }
    }

    private void updateBackup() {
        viewInfo.backup = "";
        for (String serverName : servers.keySet()) {
            // primary can not be backup
            if (!serverName.equals(viewInfo.primary)) {
                viewInfo.backup = serverName;
                break;
            }
        }
        approved = false;
        viewInfo.view += 1;
    }

}
