package queue;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * Producer-Consumer queue
 */
public class Queue extends SyncPrimitive {

	/**
	 * Constructor of producer-consumer queue
	 *
	 * @param address
	 * @param name
	 */
	public Queue(String address, String name) {
		super(address);
		this.root = name;
		
		if (zk != null) {
			try {
				// Verifies if already exists a ZNode with the specified name
				Stat stat = zk.exists(root, false);
				if (stat == null) {
					// Create root ZNode
					zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
				}
			} catch (KeeperException e) {
				System.out.println("Keeper exception when instantiating queue: "
						+ e.toString());
			} catch (InterruptedException e) {
				System.out.println("Interrupted exception");
			}
		}
	}

	/**
	 * Add element to the queue.
	 *
	 * @param i the element to be added
	 * @return
	 */
	public boolean produce(int i) throws KeeperException, InterruptedException{
		ByteBuffer b = ByteBuffer.allocate(4);
		byte[] value;

		// Add child with value i
		b.putInt(i);
		value = b.array();
		// Create ZNode
		String path = zk.create(root + "/element", value, Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT_SEQUENTIAL);
		System.out.println("Created: " + path + " - " + i);
		
		return true;
	}


	/**
	 * Remove first element from the queue.
	 *
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public int consume() throws KeeperException, InterruptedException{
		int output = -1;
		Stat stat = null;

		// Get the first element available
		while (true) { 
			synchronized (mutex) {
				// Get all ZNode children
				List<String> list = zk.getChildren(root, true);
				
				if (list.size() == 0) {
					// There is no element to be consumed
					System.out.println("Going to wait");
					mutex.wait();
				} else {
					// Selects min value
					String minStr = list.get(0).substring(7);
					Integer min = Integer.valueOf(minStr);
					for (int i = 1; i < list.size(); i++) {
						String tempStr = list.get(i).substring(7);
						Integer temp = new Integer(tempStr);
						
						if (temp < min) {
							minStr = tempStr;
						}
					}
					
					// Extracted min value
					System.out.println("Min value: " + root + "/element" + minStr);

					// Get ZNode attached data
					byte[] b = zk.getData(root + "/element" + minStr, false, stat);
					
					// Remove from queue
					zk.delete(root + "/element" + minStr, 0);
					
					ByteBuffer buffer = ByteBuffer.wrap(b);
					output = buffer.getInt();

					return output;
				}
			}
		}
	}
}