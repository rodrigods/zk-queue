package queue;
import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class SyncPrimitive implements Watcher {

	protected Integer mutex;
    protected String root;
    protected ZooKeeper zk;
    

    public SyncPrimitive(String address) {
        try {
            System.out.println("Starting ZK:");
            
            // Create ZooKeeper service
            this.zk = new ZooKeeper(address, 20000, this);
            // Initialize mutex (thread safety)
            this.mutex = new Integer(-1);
            
            System.out.println("Finished starting ZK: " + this.zk);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    
    public synchronized void process(WatchedEvent event) {
        synchronized (mutex) {
            mutex.notify();
        }
    }
}