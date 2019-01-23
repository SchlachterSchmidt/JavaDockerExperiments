import java.util.ArrayList;
import java.util.List;

public class MemEat {
    public static void main(String[] args) {
        List<byte[]> l = new ArrayList<>();
        while (true) {
            byte b[] = new byte[1048576];
            l.add(b);
            Runtime rt = Runtime.getRuntime();
            System.out.println( "free memory: " + rt.freeMemory() );
        }
    }
}
