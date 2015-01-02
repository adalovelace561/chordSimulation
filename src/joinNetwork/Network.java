package joinNetwork;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

public class Network {
	
	//N: nodes in network, M number of bits in hash, base: 2 in paper
	//circle: modulus around which circle is based: pow(base,m)
	final int initialN, M, BASE, circle, totalN;
	private Hashtable<Integer, Node> nodes = new Hashtable<Integer, Node> ();
	Node[] initialNodes;
	Random rand = new Random();

	
	Network(int N, int M){
		this.M = M;
		this.initialN = N;
		BASE   = 2;
		circle = (int) Math.pow(BASE, M);
		initialNodes  = new Node[this.initialN];
		totalN = N;
	}
	
	void populateNetwork(){
		initialNodes = new Node[initialN];
		for (int i = 0; i < initialN; i++){
			//If the hash is important change here:
			int nodeId = rand.nextInt(circle);//int value between 0 (inclusive) and the specified value (exclusive)
			initialNodes[i]   = new Node(nodeId, this);
		}
		Arrays.sort(initialNodes);//ascending numerical order
		//initialize successors
		for(int i = 0; i < initialNodes.length; i++){
			int successorIndex = i + (initialNodes.length +1 );
			int predesessorIndex = i + (initialNodes.length -1);
			initialNodes[i].setPredecessor(initialNodes[predesessorIndex % initialNodes.length]);
			initialNodes[i].setSuccessor(initialNodes[successorIndex % initialNodes.length]);
		}
		for(int i = 0; i < initialNodes.length; i++){
			initialNodes[i].populateFingers(this);
		}
	}
	
	static void insertNetworkTest(){
		int initialN = 2;
		Network net = new Network(initialN, 6);
		
		int[] testNodeID = {1, 8, 14, 21, 32, 38, 42, 48, 51, 56, 63};
		for (int i = 0; i < initialN; i++){
			net.initialNodes[i] = new Node(testNodeID[i], net);
			net.nodes.put(net.initialNodes[i].nodeId, net.initialNodes[i]);
		}
		Arrays.sort(net.initialNodes);//Sorts the specified array into ascending numerical order
		//initialize successors
		for(int i = 0; i < net.initialNodes.length; i++){
			int successorIndex = i + (net.initialNodes.length +1 );
			int predesessorIndex = i + (net.initialNodes.length -1);
			net.initialNodes[i].setPredecessor(net.initialNodes[predesessorIndex % net.initialNodes.length]);
			net.initialNodes[i].setSuccessor(net.initialNodes[successorIndex % net.initialNodes.length]);
		}
		for(int i = 0; i < net.initialNodes.length; i++){
			net.initialNodes[i].populateFingers(net);
		}

		for (int i = 2; i < testNodeID.length; i++){
			net.insertNode(net.nodes.get(testNodeID[1]), testNodeID[i]);
			System.err.println("inserting: "+i);
		}
		Iterator<Node> it = net.nodes.values().iterator();
		while(it.hasNext()){
			Node next = it.next();
			System.err.println(next);
		}
	}
	
	public int insertNode(Node initiator, int newNodeID){
		Node n = new Node(newNodeID, this);
		int querries = n.join(initiator);
		nodes.put(n.nodeId, n);
		return querries;
	}
	
	public static void main(String[] a){
		insertNetworkTest();
	}
	
	
	/**Returns the predecessor of the id. 
	 * node.id < id, so if a node matches 
	 * id, it will return the predecessor
	 * of that node.
	 * 
	 * @param id of the key or node you want
	 * @param initialNodes the finger table, or network's list of nodes
	 * @param initialN the number of nodes in the circle
	 * @return
	 */
	Node closestPreceedingNode(int id){
		//index of the search key else (-(insertion point) - 1)
		int index = Arrays.binarySearch(initialNodes, id);
		if( index < 0 ){
			index = -1*index + (initialNodes.length - 2);
		}else{
			index = index + (initialNodes.length - 1);
		}
		return initialNodes[index % initialNodes.length];
	}
}
