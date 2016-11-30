package finalProject;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;

public class NodeContext implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
	context.setId("nodes");
	
	NetworkBuilder netBuilder = new NetworkBuilder<Object>
		("node network", context, true);
	netBuilder.buildNetwork();
	
	DirectedSparseMultigraph<MyNode,MyEdge> g = GraphLoader.importGraph();
	g.getVertices();
	
	context.add(new MyNode());
		return context;
	}

}
