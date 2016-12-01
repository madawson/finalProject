package finalProject;

public class LearningAgent extends Agent {
	
	public LearningAgent(NodeSelector nodeSelector, RouteFinder routeFinder){
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
		path = routeFinder.getRoute(startNode, endNode);
	}

}
