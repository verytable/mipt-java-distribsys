import com.sun.deploy.util.StringUtils;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by arseny on 30.09.14.
 */
class Memo extends UnicastRemoteObject implements MemoInterface {

    ArrayList<String> notes;

    public Memo() throws RemoteException {
        notes = new ArrayList<String>();
    }

    public String getNotes() throws RemoteException {
        return StringUtils.join(notes, "\n");
    }

    public void addNote(String note) {
        notes.add(note);
    }
}
