package networkLoad;


public class Node implements Comparable<Object> {
	final boolean TESTING = false;
	final int     nodeId;
	Node[]        fingers;
	Node          successor;
	int           circle, base;
	int           counter = 0;
	long          bytes   = 0;
	long          keys    = 0;
	Node getSuccessor()               { return this.successor; }
	void setSuccessor(Node successor) { this.successor = successor; }
	void incrimentCounter()           { counter++; }
	void incrimentBytes(long bytes)   { this.bytes+=bytes; }
	void incrimentKeys()              { keys++; }
	
	Node(int nodeId, Network net){
		this.nodeId = nodeId;
		fingers     = new Node[net.M];
		this.circle = net.circle;
		this.base   = net.BASE;
	}
	
	void populateFingers(Network net){
		fingers[0] = successor;
		for(int i = 1; i< fingers.length; i++){
			int searchingID = (nodeId + (int) Math.pow(net.BASE, i)) % net.circle;
			fingers[i] = net.closestPreceedingNode(searchingID).successor;
		}
	}

/**
 * returns the number of nodes queried
 * @param querry
 * @param networkStats
 * @return
 */
	int itteritiveQueery(int querry){
		Node node = this;
		node.incrimentCounter();
		int numQueried = 1;
		if(node.nodeId == querry){
			if(TESTING)
				System.err.println("Terminating at: "+ node.nodeId+"\n\n");
			return numQueried;
		}
		
		if(TESTING)
			System.err.println("Queerying starting: "+ node.nodeId +" for: "+querry);		
		
		int upperBound = node.successor.nodeId, middle = querry, lowerBound = node.nodeId;
		upperBound = (upperBound - lowerBound + circle) % circle;
		middle     = (middle     - lowerBound + circle) % circle;
		lowerBound =  lowerBound - lowerBound;

		while(! ((lowerBound < middle) && (middle <= upperBound))){ //query not between node node.sucessor <= inclusive)
			if(TESTING)
				System.err.println("upperBound "+upperBound+" middle "+middle+" lowerbound "+lowerBound);
			
			numQueried++;
			node = node.closestPreceedingFinger(querry);
			node.incrimentCounter();
			if(TESTING)
				System.err.println("Queerying: "+ node.nodeId);

			
			
			upperBound = node.successor.nodeId; middle = querry; lowerBound = node.nodeId;
			upperBound = (upperBound - lowerBound + circle) % circle;
			middle     = (middle     - lowerBound + circle) % circle;
			lowerBound =  lowerBound - lowerBound;
		}
		node = node.successor;
		numQueried++;
		node.incrimentCounter();
		if(TESTING)
			System.err.println("Terminating at: "+ node.nodeId+"\n\n");
		return numQueried;
	}
	
	
	/**Returns the predecessor of the id. 
	 * node.id < id, so if a node matches 
	 * id, it will aim to return the predecessor
	 * of that node.
	 * 
	 * @param id of the key or node you want, can't be equal to node.id
	 * @param nodes the finger table, or network's list of nodes
	 * @param N the number of nodes in the circle
	 * @return
	 */
	Node closestPreceedingFinger(int id){
		if(this.nodeId == id)
			throw new RuntimeException();
		int upperBound=0, middle=0, lowerBound = 0;
		for(int i = fingers.length-1; i > -1 ; i--){
			upperBound = id; middle = fingers[i].nodeId; lowerBound = this.nodeId;
			upperBound = (upperBound - lowerBound + circle) % circle;
			middle     = (middle     - lowerBound + circle) % circle;
			lowerBound = lowerBound - lowerBound;
			if((lowerBound < middle) && (middle < upperBound)){
				return fingers[i];
			}
		}
		//throw new RuntimeException("Something is terribly wrong");	
		return this;
	}
	
	Node closestPreceedingFinger(Node id){
		return closestPreceedingFinger(id.nodeId);
	}
	
	public int compareTo(Object o) {
		if(o instanceof Integer){
			Integer compareMe = (Integer)o;
			return this.nodeId - compareMe;
		}
		if( ! (o instanceof Node))
			throw new RuntimeException("trying to compare "+o+" as Node");
		Node n = (Node) o;
		return this.nodeId - n.nodeId;
	}

}
