import java.rmi.Naming;

/**
 * Created by arseny on 30.09.14.
 */
public class MemoClient {
    public static void main(String[] args) {
        try {
            NotesCollectionInterface memo =
                (NotesCollectionInterface) Naming.lookup("memo");
            memo.addNote("Hello");
            System.out.print(memo.getNotes());
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.getMessage());
        }
    }
}
