package finalProject;

import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class RouteFinder {
	
	List<MyEdge> path;
	DijkstraShortestPath<MyNode,MyEdge> shortestPath;
	
	public RouteFinder(DirectedSparseMultigraph<MyNode,MyEdge> g){
		
		shortestPath = new DijkstraShortestPath(g);
	}
	
	public List<MyEdge> getRoute(MyNode startNode, MyNode endNode){
		path = shortestPath.getPath(startNode, endNode);
		return path;
	}

}
