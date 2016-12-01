package finalProject;

import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;

public class RouteFinder {
	
	List<MyEdge> path;
	DijkstraShortestPath<MyNode,MyEdge> shortestPath;
	Double weight;
		
	public RouteFinder(DirectedSparseMultigraph<MyNode,MyEdge> g){
	    
		//Edge Transformer
	    Transformer<MyEdge, Double> t = new Transformer<MyEdge, Double>(){
	            public Double transform(MyEdge e){
	            		weight = e.getWeight();
	            		return weight;
	            }
	    };
		
		shortestPath = new DijkstraShortestPath<MyNode, MyEdge>(g, t);
	}
	
	public List<MyEdge> getRoute(MyNode startNode, MyNode endNode){
		path = shortestPath.getPath(startNode, endNode);
		return path;
	}

}
