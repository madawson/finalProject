package finalProject;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class FinalProjectBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		
		context.setId("finalProject");
		
		DirectedSparseMultigraph<MyNode,MyEdge> g = GraphLoader.importGraph();	
		
		ArrayList<MyEdge> edges = new ArrayList<MyEdge>(g.getEdges());
		
		for(int i = 0; i < edges.size(); i++){
			context.add(edges.get(i));
		}
		
		NodeSelector nodeSelector = new NodeSelector(g);
		RouteFinder routeFinder = new RouteFinder(g);
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int agentCount = params.getInteger("agent_count");
		int learningAgentCount = params.getInteger("learning_agent_count");
		
		Supervisor supervisor = new Supervisor((agentCount + learningAgentCount));		
		
		for (int i = 0; i < agentCount; i++) {
			context.add(new Agent(nodeSelector, routeFinder, supervisor));
		}
		

		for(int i = 0; i < learningAgentCount; i++){
			context.add(new LearningAgent(nodeSelector, routeFinder, supervisor));
		}
		
		context.add(supervisor);
				
		return context;
	}

}
