package finalProject;

import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

public class RouteFinder {
	
	Transformer<MyEdge, Double> t;
	List<MyEdge> pathOne;
	List<MyEdge> pathTwo;
	DijkstraShortestPath<MyNode,MyEdge> shortestPath;
	DijkstraShortestPath<MyNode,MyEdge> secondShortestPath;
	Double weight;
	static DirectedSparseMultigraph<MyNode,MyEdge> graph;
		
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
