import java.rmi.Naming;

/**
 * Created by arseny on 30.09.14.
 */
class AdderClient {
    public static void main(String[] args) {
        try {
            /*
            * На кленте есть заглушка
            * которую возвращает lookup
            * Эта заглушка реализует интерфейс adder
            * */
            AdderInterface adder =
                    (AdderInterface) Naming.lookup("adder");
            int x = adder.add(3, 4);
            System.out.println(x);
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.getMessage());
        }
    }
}
