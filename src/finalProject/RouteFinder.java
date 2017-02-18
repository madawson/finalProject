package finalProject;

import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

public class RouteFinder {
	
	/*The route finder class is used to calculate an optimal shortest route and a sub-optimal route, along with the live total
	weight of each route.
	 */
	
	private DirectedSparseMultigraph<MyNode,MyEdge> graph;
	private DijkstraShortestPath<MyNode,MyEdge> shortestPath;
	private DijkstraShortestPath<MyNode,MyEdge> secondShortestPath;
	private Double weight;
	private Transformer<MyEdge, Double> t;
	private List<MyEdge> pathOne;
	private List<MyEdge> pathTwo;
		
	public RouteFinder(DirectedSparseMultigraph<MyNode,MyEdge> g){
	    
		//Create edge Transformer
	    t = new Transformer<MyEdge, Double>(){
	            public Double transform(MyEdge e){
	            		weight = e.getWeight();
	            		return weight;
	            }
	    };
		
	    //Set up the shortest path object.
	    graph = g;
		shortestPath = new DijkstraShortestPath<MyNode, MyEdge>(g, t);
		
	}
	
	public List<MyEdge> getRoute(MyNode startNode, MyNode endNode){
		pathOne = shortestPath.getPath(startNode, endNode);
		return pathOne;
	}
	
	public List<MyEdge> getSecondRoute(MyEdge e, MyNode startNode, MyNode endNode){
		Pair<MyNode> endPoints = graph.getEndpoints(e);
		graph.removeEdge(e);
		secondShortestPath = new DijkstraShortestPath<MyNode, MyEdge>(graph, t);
		pathTwo = secondShortestPath.getPath(startNode, endNode);
		graph.addEdge(e, endPoints);
		return pathTwo;
	}
	
	public double getDistance(MyNode startNode, MyNode endNode){
		Number distance = shortestPath.getDistance(startNode, endNode);
		return distance.doubleValue();
	}
	
	public double getSecondDistance(MyEdge e, MyNode startNode, MyNode endNode){
		Pair<MyNode> endPoints = graph.getEndpoints(e);
		graph.removeEdge(e);
		secondShortestPath = new DijkstraShortestPath<MyNode, MyEdge>(graph, t);
		Number distance = secondShortestPath.getDistance(startNode, endNode);
		graph.addEdge(e, endPoints);
		return distance.doubleValue();
	}
	
}
