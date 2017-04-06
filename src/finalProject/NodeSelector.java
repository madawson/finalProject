package finalProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class NodeSelector {
	
	/*The node selector class is used to select either an 'S' type node or an 'L' type node depending on predetermined probabilities.
	  The probabilities are currently set at:
	  'S': 0.3
	  'L': 0.7
	 */
	
	/**
	 * Stores the list of all nodes found in the provided graph.
	 */
	List<MyNode> nodeList;
	
	/**
	 * Stores the list of all nodes of type 'S' found in the provided graph.
	 */
	List<MyNode> sList;
	
	/**
	 * Stores the list of all nodes of type 'N' found in the provided graph.
	 */
	List<MyNode> lList;
	
	public NodeSelector(DirectedSparseMultigraph<MyNode,MyEdge> g){
		
		Collection<MyNode> nodes = g.getVertices();
		nodeList = new ArrayList<MyNode>(nodes);
		sList = new ArrayList<MyNode>();
		lList = new ArrayList<MyNode>();
		
		//Built the subset of 'S' nodes and the subset of 'L' nodes.
		MyNode currentNode;
		String checkType;
		
		String sType = "S";
		String lType = "L";
		
		for(int i= 0; i < nodeList.size(); i++){
			currentNode = nodeList.get(i);
			checkType = currentNode.getType();
			if(checkType.equals(sType))
				sList.add(currentNode);
			else if(checkType.equals(lType))
				lList.add(currentNode);
		}
	}
	
	/**
	 * Selects either type 'S' or 'L' with given probability and then a node from the chosen list at random.
	 * @return a node.
	 */
	public MyNode getNode() {
		
		//Probabilistically select a specific node.
		Random rnd = new Random();
		int index;
		MyNode node;
		
		if(Math.random()<0.3){
			index = rnd.nextInt(sList.size());
			node = sList.get(index);
		}
		else{
			index = rnd.nextInt(lList.size());
			node = lList.get(index);
		}
			
		return node;
	}
}
