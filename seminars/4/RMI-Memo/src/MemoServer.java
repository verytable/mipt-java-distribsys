import java.rmi.Naming;

/**
 * Created by arseny on 30.09.14.
 */
class MemoServer {
    public static void main(String[] args) throws RuntimeException {
        try {
            NotesCollection notesCollection = new NotesCollection();
            Naming.rebind("memo", notesCollection);
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.getMessage());
        }
    }
}
