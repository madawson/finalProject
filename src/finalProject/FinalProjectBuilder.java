package finalProject;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class FinalProjectBuilder implements ContextBuilder<Object> {

	/**
	 * Build method is required by the RePast ContextBuilder interface. This method creates instances of all the required objects
	 * that are to take part in a simulation. This includes: the nodeSelector, the routeFinder, the supervisor, all standard agents 
	 * and all learning agents. All of these objects are added to the RePast Context. All edge objects that are found within the 
	 * imported graph are also added to the Context.
	 */
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
			LearningAgent learningAgent = new LearningAgent(nodeSelector, routeFinder, supervisor);
			context.add(learningAgent);
		}
		
		context.add(supervisor);
		
		return context;
	}

}
