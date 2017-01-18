package finalProject;

import java.util.List;
import java.util.HashMap;

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
	HashMap<Double, Double> probabilityTable;
		
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
		
		//Construct the probability table.
		probabilityTable = new HashMap<Double, Double>();
		probabilityTable.put(0.0, 0.048);
		probabilityTable.put(1.0, 0.014);
		probabilityTable.put(2.0, 0.010);
		probabilityTable.put(3.0, 0.014);
		probabilityTable.put(4.0, 0.048);
		probabilityTable.put(5.0, 0.119);
		probabilityTable.put(6.0, 0.476);
		probabilityTable.put(7.0, 0.833);
		probabilityTable.put(8.0, 0.833);
		probabilityTable.put(9.0, 0.643);
		probabilityTable.put(10.0, 0.524);
		probabilityTable.put(11.0, 0.548);
		probabilityTable.put(12.0, 0.571);
		probabilityTable.put(13.0, 0.619);
		probabilityTable.put(14.0, 0.667);
		probabilityTable.put(15.0, 0.714);
		probabilityTable.put(16.0, 0.952);
		probabilityTable.put(17.0, 1.0);
		probabilityTable.put(18.0, 0.714);
		probabilityTable.put(19.0, 0.476);
		probabilityTable.put(20.0, 0.238);
		probabilityTable.put(21.0, 0.167);
		probabilityTable.put(22.0, 0.095);
		probabilityTable.put(23.0, 0.048);
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

	public double getProbability(double tick){	
		double i = tick % 1440;
		i = i/60;
		return probabilityTable.get(Math.floor(i));
	}
}
