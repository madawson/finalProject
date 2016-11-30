package finalProject;

public class LearningAgent extends Agent {
	
	public LearningAgent(NodeSelector nodeSelector, RouteFinder routeFinder){
		nodeSelector = this.nodeSelector;
		routeFinder = this.routeFinder;
		
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
		path = routeFinder.getRoute(startNode, endNode);
	}

}
