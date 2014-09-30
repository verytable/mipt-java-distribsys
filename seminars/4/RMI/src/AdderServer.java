import java.rmi.Naming;

/**
 * Created by arseny on 30.09.14.
 */
class AdderServer {
    public static void main(String[] args) throws RuntimeException {
        try {
            Adder adder = new Adder();
            Naming.rebind("rmi://localhost/adder", adder); //если к этому имени уже привязан класс, он перепривяжется
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.getMessage());
        }
    }
}
