package finalProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class NodeSelector {
	
	String type;
	List<MyNode> nodeList;
	
	public NodeSelector(DirectedSparseMultigraph<MyNode,MyEdge> g){
		
		Collection<MyNode> nodes = g.getVertices();
		nodeList = new ArrayList<MyNode>(nodes);
		
	}
	
	public MyNode getNode() {
		
		Random rnd = new Random();
		float number = rnd.nextFloat();

		if(number<0.3)
			type = "S";		
		else
			type = "L";
		
		MyNode container;
		String checkType;
		List<MyNode> subSetList = new ArrayList<MyNode>();
		for(int i= 0; i < nodeList.size(); i++){
			container = nodeList.get(i);
			checkType = container.getType();
			if(checkType == type)
				subSetList.add(container);
		}
		
		int index = rnd.nextInt(subSetList.size() + 1);
		MyNode node = subSetList.get(index);
		
		return node;
	}
}
