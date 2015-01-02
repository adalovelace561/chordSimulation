package joinNetwork;


public class Node implements Comparable<Object>  {
	final boolean TESTING = false;
	static boolean INSERTING = false;
	int           timesQuerried = 0, timesQuerriedINSERTING = 0;
	void timesQuerried(){ 
		if(INSERTING)
			timesQuerriedINSERTING++;
		else
			timesQuerried++;
	}



	final int     nodeId;
	Node[]       fingers;
	int           circle, base, M;
	Node         successor;
	Node getSuccessor()               { return this.successor; }
	void setSuccessor(Node successor) { this.successor = successor; }

	Node         predecessor;
	Node getPredecessor(){
		return this.predecessor; 
	}
	void setPredecessor(Node predecessor) { this.predecessor = predecessor; }


	Node(int nodeId, Network net){
		this.nodeId = nodeId;
		fingers     = new Node[net.M];
		this.circle = net.circle;
		this.base   = net.BASE;
		this.M      = net.M;
	}


	public int join(Node n2){
		INSERTING = true;
		int numQuerries=0;
		if(n2!= null){
			//numQuerries += initFingerTable(n2);
			initFingerTable(n2);
			numQuerries += updateOthers();
			//move keys in (predecessor, n] from successor
		}else{
			for(int i =0; i < this.fingers.length; i++){
				fingers[i] = this;
			}
			this.setPredecessor(this);
		}
		
		INSERTING = false;
		return numQuerries;
	}


	private int initFingerTable(Node n2){
		int numQuerries=0;
		//
		Object[] temp =n2.findPredecessor((this.nodeId+1%circle));
		numQuerries+=(Integer)temp[1];
		this.fingers[0] = ((Node)temp[0]).successor;
		this.successor = this.fingers[0];
		this.predecessor = this.successor.predecessor;

		this.successor.setPredecessor(this);
		this.successor.timesQuerried();
		numQuerries++;

		for(int i =0; i < M-1; i++){
			int nextID = (int) (this.nodeId + Math.pow(base, i+1));
			int upperBound = fingers[i].nodeId, middle = nextID, lowerBound = this.nodeId;
			upperBound = (upperBound - lowerBound + circle) % circle;
			middle     = (middle     - lowerBound + circle) % circle;
			lowerBound =  lowerBound - lowerBound;
			if((lowerBound <= middle) && (middle < upperBound)){
				this.fingers[i+1] = this.fingers[i];
			}else{

				temp =n2.findPredecessor((this.nodeId+1%circle));
				this.fingers[i+1] = ((Node)temp[0]).successor;
				numQuerries+=(Integer)temp[1];
			}
		}
		return numQuerries;
	}

	private int updateOthers(){
		int numQuerries=0;
		for(int i =0; i< M; i++){
			int id = (this.nodeId- (int)Math.pow(base, i)+circle)%circle;
			Object[] temp = this.findPredecessor(id);
			numQuerries += (Integer)temp[1];
			Node p = (Node)temp[0];
			numQuerries += p.updateFingerTable(this, i);
		}
		//the below line not specified in the protocol
		this.predecessor.setSuccessor(this);
		return numQuerries++;
	}
	//this is when things start getting queried
	private int updateFingerTable(Node s, int i){
		int numQuerries=1;
		int upperBound = fingers[i].nodeId, middle = s.nodeId, lowerBound = this.nodeId;
		upperBound = (upperBound - lowerBound + circle) % circle;
		middle     = (middle     - lowerBound + circle) % circle;
		lowerBound =  lowerBound - lowerBound;
		if((lowerBound == upperBound)||((lowerBound <= middle) && (middle < upperBound))){
			//System.err.println("\trecursed with: upperBound "+upperBound+" middle "+middle+" lowerbound "+lowerBound);
			if(!this.equals(s))
				fingers[i] = s;
			else
				return numQuerries;
			Node p = this.getPredecessor();
			numQuerries+=p.updateFingerTable(s, i);
		}
		return numQuerries;
	}



	Object[] findPredecessor(int querry){
		Node node = this;
		//node.timesQuerried();
		int numQueried = 0;
		if(node.nodeId == querry){
			throw new RuntimeException(this.toString());
		}
		int upperBound = node.successor.nodeId;
		int middle = querry;
		int lowerBound = node.nodeId;
		upperBound = (upperBound - lowerBound + circle) % circle;
		middle     = (middle     - lowerBound + circle) % circle;
		lowerBound =  lowerBound - lowerBound;

		while(! ((lowerBound < middle) && (middle <= upperBound))){ //query not between node node.sucessor <= inclusive)
			Node temp = node;
			numQueried++;
			node = node.closestPreceedingFinger(querry);
			node.timesQuerried();
			//XXX: changed functionality need to consider tomorrow
			if(node.nodeId == temp.nodeId)
				break;

			upperBound = node.successor.nodeId; middle = querry; lowerBound = node.nodeId;
			upperBound = (upperBound - lowerBound + circle) % circle;
			middle     = (middle     - lowerBound + circle) % circle;
			lowerBound =  lowerBound - lowerBound;
		}
		Object[] ret = {node, numQueried};
		return ret;
	}
	/**
	 * returns the number of nodes queried
	 * @param querry
	 * @param networkStats
	 * @return
	 */
	int itteritiveQueery(int querry){
		Node node = this;
		node.timesQuerried();
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
			node.timesQuerried();
			if(TESTING)
				System.err.println("Queerying: "+ node.nodeId);

			upperBound = node.successor.nodeId; middle = querry; lowerBound = node.nodeId;
			upperBound = (upperBound - lowerBound + circle) % circle;
			middle     = (middle     - lowerBound + circle) % circle;
			lowerBound =  lowerBound - lowerBound;
		}
		node = node.successor;
		numQueried++;
		node.timesQuerried();
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
		//System.err.println("closest preceeding finger from: "+this.nodeId+" queerrying for predecessor of: "+id);
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
		return this;
	}

	Node closestPreceedingFinger(Node id){
		return closestPreceedingFinger(id.nodeId);
	}


	void populateFingers(Network net){
		fingers[0] = successor;
		for(int i = 1; i< fingers.length; i++){
			int searchingID = (nodeId + (int) Math.pow(net.BASE, i)) % net.circle;
			fingers[i] = net.closestPreceedingNode(searchingID).successor;
		}
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
	public String toString(){
		String s = "node: "+nodeId;
		s+=" successor: "+successor.nodeId;
		s+=" predecessor "+predecessor.nodeId ;
		for(int j = 0; j < fingers.length; j++){
			s+="\n\t\tfinger: "+j+" is: "+fingers[j].nodeId;
		}
		return s;
	}
}

