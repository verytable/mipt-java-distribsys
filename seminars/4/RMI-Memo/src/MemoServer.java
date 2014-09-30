import java.rmi.Naming;

/**
 * Created by arseny on 30.09.14.
 */
class MemoServer {
    public static void main(String[] args) throws RuntimeException {
        try {
            Memo memo = new Memo();
            Naming.rebind("memo", memo);
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.getMessage());
        }
    }
}
