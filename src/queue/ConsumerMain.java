package queue;
import org.apache.zookeeper.KeeperException;


public class ConsumerMain {
	public static void main(String args[]) {
		// Create a new queue
		Queue q = new Queue(args[0], "/app1");
		System.out.println("Input: " + args[0]);
		System.out.println("Consumer");

		while (true) {
			try{
				int r = q.consume();			
				System.out.println("Item: " + r);
			} catch (KeeperException e){
        		System.out.println(e.getStackTrace());
        	} catch (InterruptedException e){
        		System.out.println(e.getStackTrace());
        	}
		}
	}

}
