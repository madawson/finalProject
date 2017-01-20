package finalProject;

import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class RouteFinder {
	
	Transformer<MyEdge, Double> t;
	List<MyEdge> pathOne;
	List<MyEdge> pathTwo;
	DijkstraShortestPath<MyNode,MyEdge> shortestPath;
	DijkstraShortestPath<MyNode,MyEdge> secondShortestPath;
	Double weight;
	DirectedSparseMultigraph<MyNode,MyEdge> graph;
		
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
	
	public List<MyEdge> getSecondRoute(DirectedSparseMultigraph<MyNode,MyEdge> alteredGraph, MyNode startNode, MyNode endNode){
		secondShortestPath = new DijkstraShortestPath<MyNode, MyEdge>(alteredGraph, t);
		pathTwo = secondShortestPath.getPath(startNode, endNode);
		return pathTwo;
	}
	
	public DirectedSparseMultigraph<MyNode,MyEdge> getGraph(){
		return graph;
	}
	
}
