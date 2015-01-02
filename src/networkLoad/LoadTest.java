package networkLoad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class LoadTest {
	final int NumNodes = 2000, HashLength = 30;
	Random  rand = new Random(); Network net;
	//DescriptiveStatistics networkStats = new DescriptiveStatistics();
	DescriptiveStatistics nodeStats    = new DescriptiveStatistics();
	//DescriptiveStatistics querryStats  = new DescriptiveStatistics();

	//0 type resource, 1 name resource, 2 times queried, 3 size
	//String INPUTFILENAME = "pagecounts-pretend.txt";
	final String INPUTFILENAME = "pagecounts-20141101-000000.txt";
	
	//node, number of times traversed: sorted by nodeId
	String nodetraversedstats = "nodeStats-"+NumNodes+".txt";
	FileWriter writerNodeTraversed = null;
	
	//number of hops each querry took, only stores the number of hops
	String OUTNetStats = "netstats-"+NumNodes+".txt";
	FileWriter writerNetStats = null;
	
	//querry, numQueeried: unsorted hash of querry
//	String OUTQuerryStats = "querrystats-"+N+".txt";
//	FileWriter writerQuerryStats = null; 

	//node, number of times traversed: sorted by nodeId
	String nodebytestats = "nodeByteStats-"+NumNodes+".txt";
	FileWriter writerNodeBytes = null;

	//node, number of times traversed: sorted by nodeId
	String nodeKeystats = "nodeKeyStats-"+NumNodes+".txt";
	FileWriter writerNodeKey = null;
	
	
	public LoadTest() throws IOException{
		File file = new File(OUTNetStats);           
		if (!file.exists()) {
			file.createNewFile();
		}
		writerNetStats = new FileWriter(file);

		File file1 = new File(nodetraversedstats);           
		if (!file1.exists()) {
			file1.createNewFile();
		}
		writerNodeTraversed = new FileWriter(file1);
		writerNodeTraversed.write("node numtimestraversed\n");
		
//		File file2 = new File(OUTQuerryStats);
//		if (file2.exists()) {
//			throw new RuntimeException(OUTQuerryStats +" already exists");
//		}
//		file2.createNewFile();
//		writerQuerryStats = new FileWriter(file2);
//		writerQuerryStats.write("querry numtimesquerried\n");
		
		File file3 = new File(nodebytestats);           
		if (!file3.exists()) {
			file3.createNewFile();
		}
		writerNodeBytes = new FileWriter(file3);
		writerNodeBytes.write("node numbytesstored\n");
		
		File file4 = new File(nodeKeystats);           
		if (!file4.exists()) {
			file4.createNewFile();
		}
		writerNodeKey = new FileWriter(file4);
		writerNodeKey.write("node numkeysstored\n");
	}

	public void testWiki() throws IOException{
		net  = new Network(NumNodes, HashLength);
		net.populateNetwork();
		testSpecific();
		System.err.println("getting stats from: "+net.nodes.length);
		for(int i =0; i < net.nodes.length; i++){
			nodeStats.addValue(net.nodes[i].counter);
			writerNodeTraversed.write("\n"+net.nodes[i].nodeId+","+net.nodes[i].counter);
			writerNodeBytes.write("\n"+net.nodes[i].nodeId+","+net.nodes[i].bytes);
			writerNodeKey.write("\n"+net.nodes[i].nodeId+","+net.nodes[i].keys);
		}
		writerNodeTraversed.close(); 
		//System.err.println("Network statistics"+networkStats);
		System.err.println("node statistics"+nodeStats);
		//System.err.println("querry statistics"+querryStats);
		//writerQuerryStats.close();
		writerNodeTraversed.close();
		writerNetStats.close();
		writerNodeBytes.close();
		writerNodeKey.close();
	}

	/**
	 * this is to test with the wiki input file
	 * @throws IOException 
	 */
	public void testSpecific() throws IOException{
		MessageDigest md;
		try { md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e1) { throw new RuntimeException(e1); }
		BufferedReader macroInput = new BufferedReader(new FileReader(INPUTFILENAME));
		String[] line;
		String line1 = "";
		String line2 = "";
		String line3 = "";
		String s = macroInput.readLine();
		while(s!= null) {
			s = s.replace("%","__");
			s = s.replace("(","__");
			s = s.replace(")","__");
			s = s.replace(",","__");
			s = s.replace("\'","__");
			s = s.replace("{","__");
			s = s.replace("}","__");
			s = s.replace(";","__");
			s = s.replace("=","__");
			s = s.replace("&","__");
			s = s.replace("\\","__");
			s = s.replace("!","__");
			s = s.replace("?","__");
			s = s.replace("\"","__");
			s = s.replace("'","__");
			s = s.replace("--","__");
			s = s.replace("-","__");
			s = s.replace("*","__");
			s = s.replace("+","__");
			s = s.replace("~","__");
			s = s.replace("[","__");
			s = s.replace("]","__");
			s = s.replace("^","__");
			s = s.replace("`","__");
			s = s.replace("|","__");
			s = s.replace("$","__");
			s = s.replace("<","__");
			s = s.replace(">","__");
			//0 type resource, 1 name resource, 2 times queried, 3 size
			line = s.split(" ");
			line1 = line[1];
			line1 = line1.replaceAll("[\u0080-\uffff]","__")+line[0];
			line2 = line[2];
			line3 = line[3];
			byte[] valQuerried = md.digest(line1.getBytes());
			int numQueeries = Integer.parseInt(line2);
			//querryStats.addValue(numQueeries);

			BigInteger temp = new BigInteger(valQuerried);
			temp = temp.mod(BigInteger.valueOf(net.circle));
			int querry = temp.intValue();
			//writerQuerryStats.write(querry+","+numQueeries+"\n");
			//writerQuerryStats.flush();
			Node node = net.closestPreceedingNode(querry).successor;
			
			node.incrimentKeys();
			node.incrimentBytes(Long.parseLong(line3));
			
			querryNtimes(numQueeries, querry);
			//			for(int i = 0; i < numQueeries; i++){
			//				int node   = rand.nextInt(net.nodes.length);
			//				//figure out if I need to make everything different... with big ints!
			//				//networkStats.addValue(net.nodes[node].itteritiveQueery(querry));
			//				writerNetStats.write("\n"+net.nodes[node].itteritiveQueery(querry));
			//			}
			s = macroInput.readLine();
		}
		System.err.println("finished parsing"+line1+" "+line2 + ""+line3);
	}

	
	public void querryNtimes(int times, int querry) throws IOException{
		for(int i = 0; i < times; i++){
			int node   = rand.nextInt(net.nodes.length);
			//networkStats.addValue(net.nodes[node].itteritiveQueery(querry));
			writerNetStats.write("\n"+net.nodes[node].itteritiveQueery(querry));
		}
		writerNetStats.flush();
	}

	public void testCorrect(){
		int numnodes = 11, msize = 6;
		Network net  = new Network(numnodes, msize);
		net.populateNetworkTest();
		net.nodes[0].itteritiveQueery(63);
		System.err.println();
		net.nodes[net.nodes.length-1].itteritiveQueery(56);
		net.nodes[1].itteritiveQueery(54);
		net.nodes[net.nodes.length-1].itteritiveQueery(63);
		net.nodes[net.nodes.length-1].itteritiveQueery(62);
	}

	public static void main(String[] s){
		LoadTest t = null;
		try {
			t = new LoadTest();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			t.testWiki();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}