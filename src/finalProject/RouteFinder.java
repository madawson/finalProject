package finalProject;

import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

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
	
	/**
	 * Obtain an optimal path between the provided start node and end node.
	 * @param startNode is the node at the start of the path.
	 * @param endNode is the node at the end of the path.
	 * @return an optimal path between the start node and the end node.
	 */
	public List<MyEdge> getShortestRoute(MyNode startNode, MyNode endNode){
		pathOne = shortestPath.getPath(startNode, endNode);
		return pathOne;
	}
	
	/**
	 * Obtain a sub-optimal route between the provided start node and end node.
	 * @param e is an edge that is present in the optimal path, which will not be present in the sub-optimal path.
	 * @param startNode is the node at the start of the path.
	 * @param endNode is the node at the end of the path.
	 * @return a sub-optimal route, that does not contain edge e.
	 */
	public List<MyEdge> getSubOptimalRoute(MyEdge e, MyNode startNode, MyNode endNode){
		Pair<MyNode> endPoints = graph.getEndpoints(e);
		graph.removeEdge(e);
		secondShortestPath = new DijkstraShortestPath<MyNode, MyEdge>(graph, t);
		pathTwo = secondShortestPath.getPath(startNode, endNode);
		graph.addEdge(e, endPoints);
		return pathTwo;
	}
	
	/**
	 * Obtain the total weight of the optimal path.
	 * @param startNode is the node at the start of the path.
	 * @param endNode is the node at the end of the path.
	 * @return the total weight of the optimal path.
	 */
	public double getDistance(MyNode startNode, MyNode endNode){
		Number distance = shortestPath.getDistance(startNode, endNode);
		return distance.doubleValue();
	}
		
	/**
	 * Used to obtain the total weight of any given path.
	 * @param path is the list of edges, which are to be inspected to find their weight.
	 * @return the total weight of all edges within the given path.
	 */	
	public double getPathLength(List<MyEdge> path){
		MyEdge e;
		double cumulativeWeight = 0.0;
		for(int i = 0; i<path.size(); i++){
			e = path.get(i);
			cumulativeWeight += e.getWeight();
		}
		return cumulativeWeight;
	}
	
}
