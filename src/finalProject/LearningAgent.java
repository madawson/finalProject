package finalProject;

public class LearningAgent extends Agent {
	
	public LearningAgent(NodeSelector nodeSelector, RouteFinder routeFinder, Supervisor supervisor){

		//Initialise the agent instance variables.
		stage = 0;			
		progress = 0;		
		active = false;
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		this.supervisor = supervisor;
		
		//Obtain the first start and end nodes.
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
		//Try to avoid having the same start and end node (not crucial).
		endNode = (startNode == endNode) ? nodeSelector.getNode() : endNode;
		
		//Obtain a path.
		path = routeFinder.getRoute(startNode, endNode);
		if(checkCongestion(path)) 
			secondPath();	
	}
}
