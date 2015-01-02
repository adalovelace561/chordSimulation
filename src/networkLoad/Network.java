package networkLoad;

import java.util.Arrays;
import java.util.Random;

public class Network {
	//N: nodes in network, M number of bits in hash, base: 2 in paper
	//circle: modulus around which circle is based: pow(base,m)
	final int N, M, BASE, circle;
	Node[] nodes;
	Random rand = new Random();
	
	
	Network(int N, int M){
		System.err.println("Building new network: M "+M+" N "+N);
		this.M = M;
		this.N = N;
		BASE   = 2;
		circle = (int) Math.pow(BASE, M);
		nodes  = new Node[this.N];
	}
	
	void populateNetwork(){
		for (int i = 0; i < N; i++){
			//If the hash is important change here:
			int nodeId = rand.nextInt(circle);//int value between 0 (inclusive) and the specified value (exclusive)
			nodes[i]   = new Node(nodeId, this);
		}
		Arrays.sort(nodes);//ascending numerical order
		//initialize successors
		for(int i = 0; i < nodes.length; i++){
			int successorIndex = i + (nodes.length +1 );
			nodes[i].setSuccessor(nodes[successorIndex % nodes.length]);
		}
		for(int i = 0; i < nodes.length; i++){
			nodes[i].populateFingers(this);
		}
	}
	
	void populateNetworkTest(){
		int[] testNodeID = {1, 8, 14, 21, 32, 38, 42, 48, 51, 56, 63};
		for (int i = 0; i < N; i++){
			nodes[i] = new Node(testNodeID[i], this);
		}
		Arrays.sort(nodes);//Sorts the specified array into ascending numerical order
		//initialize successors
		for(int i = 0; i < nodes.length; i++){
			int successorIndex = i + (nodes.length +1 );
			nodes[i].setSuccessor(nodes[successorIndex % nodes.length]);
		}
		System.err.println("Sucessors:");
		for(int i = 0; i < nodes.length; i++){
			System.err.println("\t"+nodes[i].nodeId+" => "+nodes[i].successor.nodeId);
		}
		for(int i = 0; i < nodes.length; i++){
			nodes[i].populateFingers(this);
		}
		
		System.err.println("fingertables:");
		for(int i = 0; i < nodes.length; i++){
			System.err.println("\t"+nodes[i].nodeId+" fingertable ");
			for(int j = 0; j < nodes[i].fingers.length; j++){
				System.err.println("\t\tfinger: "+j+" is: "+nodes[i].fingers[j].nodeId);
			}
		}
	}
	
	
	/**Returns the predecessor of the id. 
	 * node.id < id, so if a node matches 
	 * id, it will return the predecessor
	 * of that node.
	 * 
	 * @param id of the key or node you want
	 * @param nodes the finger table, or network's list of nodes
	 * @param NumNodes the number of nodes in the circle
	 * @return
	 */
	Node closestPreceedingNode(int id){
		//index of the search key else (-(insertion point) - 1)
		int index = Arrays.binarySearch(nodes, id);
		if( index < 0 )
			index = -1*index + (nodes.length - 2);
		else
			index = index + (nodes.length - 1);
		return nodes[index % nodes.length];
	}
}

