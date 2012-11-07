package queue;
import org.apache.zookeeper.KeeperException;


public class ProducerMain {
    public static void main(String args[]) {
    	// Create a new queue
        Queue q = new Queue(args[0], "/app1");

        // Max executions
        Integer max = new Integer(args[1]);

        System.out.println("Input: " + args[0]);
        System.out.println("Producer");

        for (int i = 0; i < max; i++) {
        	try{
        		q.produce(i*10);
        	} catch (KeeperException e){
        		System.out.println(e.getStackTrace());
        	} catch (InterruptedException e){
        		System.out.println(e.getStackTrace());
        	}
        }
    }
}
