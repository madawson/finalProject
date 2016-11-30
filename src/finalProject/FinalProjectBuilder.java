package finalProject;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class FinalProjectBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		context.setId("finalProject");
		
		DirectedSparseMultigraph<MyNode,MyEdge> g = new DirectedSparseMultigraph<MyNode, MyEdge>();
		g = GraphLoader.importGraph();
		NodeSelector nodeSelector = new NodeSelector(g);
		RouteFinder routeFinder = new RouteFinder(g);
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int agentCount = params.getInteger("agent_count");
		for (int i = 0; i < agentCount; i++) {
			context.add(new Agent(nodeSelector, routeFinder));
		}
		
		return context;
	}

}
