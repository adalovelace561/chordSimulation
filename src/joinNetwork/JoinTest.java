package joinNetwork;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class JoinTest {
	int NumNodes = 1000, HashLength = 30, NumInsert = 1000;
	
	Random  rand = new Random(); Network net;

	// File Format:
	// 0 type resource, 1 name resource, 2 times queried, 3 size
	String INPUTFILENAME = "pagecounts-20141101-000000.txt";

	//number of hops each querry took, only stores the number of hops
	String OUTJoinStats = "joinStats-"+NumNodes+"-"+NumInsert+".txt";
	FileWriter writerjoinStats = null;
	
	public JoinTest() throws IOException{
		File file = new File(OUTJoinStats);           
		if (!file.exists()) {
			file.createNewFile();
		}
		writerjoinStats = new FileWriter(file);
	}

	public void testThm3() throws IOException{
		
		net  = new Network(NumNodes, HashLength);
		net.populateNetwork();
		for(int i = 0; i < NumInsert; i++ ){
			//Node2 initiator = net.pickRandomNo.de();
			Node initiator = net.initialNodes[rand.nextInt(net.initialNodes.length)];
			int newNodeID = rand.nextInt(net.circle);
			int numquerries = net.insertNode(initiator, newNodeID);
			
			System.err.println("this is how many querried "+numquerries);
			
			writerjoinStats.write("\n"+numquerries);
			writerjoinStats.flush();
		}
		writerjoinStats.close();
	}

	public static void main(String[] s){
		JoinTest t = null;
		try {
			t = new JoinTest();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			t.testThm3();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
