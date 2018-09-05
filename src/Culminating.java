import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Palanichami
 */
public class Culminating {
    private static volatile int abc;
    public static boolean df = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Runnable r = new Runnable() {
            public void run() {
                while(true){
                    if(abc==1){
                        System.out.println("it works");
                        df = true;
                    }
                }
            }
        };
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                while(true){
                    Scanner input = new Scanner(System.in);
                    abc = input.nextInt();
                    System.out.println("abc is set to"+abc+"    "+df);
                } 
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(r2);
        executor.submit(r);

    }
    
}
