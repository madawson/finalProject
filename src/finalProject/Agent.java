package finalProject;

import java.util.List;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.schedule.Schedule;
//import repast.simphony.engine.watcher.Watch;
//import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class Agent {
	
	NodeSelector nodeSelector;
	RouteFinder routeFinder;
	MyNode startNode;
	MyNode endNode;
	List<MyEdge> path;
	List<MyEdge> secondPath;
	DirectedSparseMultigraph<MyNode,MyEdge> graph;
		
	//Variable to keep track of the agents stage  through its path.
	int stage;
		
	//Variable to measure the progress along an edge.
	double progress;
		
	//True if the agent is currently on a route.
	boolean active;
	
	double weight;
	MyEdge e;
	
	//Empty constructor required because learningAgent implements this class.
	public Agent(){		
	}

	//Agent constructor.
	public Agent(NodeSelector nodeSelector, RouteFinder routeFinder){
		
		//Initialise the agent instance variables.
		stage = 0;			
		progress = 0;		
		active = false;
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		
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
	
	
	
//------------------Step Method---------------------------------------------------------------------------------------
	
	@ScheduledMethod(start = 1, interval = 1) 
	public void step(){
		
		if(active==false){
			if(checkStart()) 
				active = true;
			else 
				return;
		}
		
		if(path.size() > 0 & stage < path.size()){

				if(progress == 0){
					e = path.get(stage);
					e.joinEdge();
				}
				weight = e.getWeight();
				//Progress is a function of edge weight.
				progress = progress + ((-0.1*weight) + 11);
				if(progress >= 100){
					e.leaveEdge();
					reset();
				}
			
		}
		else {
			reset();
		}
		

//		System.out.println("My start node is: " + startNode.getId());
//		System.out.println("My end node is: " + endNode.getId());
//		System.out.println("My path size is: " + path.size());
//		System.out.println("I'm at stage " + stage);	
//		System.out.println("My progress along this edge is " + progress);
//		System.out.println("The number of people on this edge is " + e.getNumUsers());
//		System.out.println("It is " + active + " that I am active");
//		System.out.println("I have recieved a warning: ");
	}
	

	
//------------------Utility Methods---------------------------------------------------------------------------------------
	

	private boolean checkCongestion(List<MyEdge> path){
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getThreshold() >= e.getCapacity())
				return true;
		}
		return false;
	}
	
	
	
	private MyEdge getFirstCongestedEdge(List<MyEdge> path){
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getThreshold() >= e.getCapacity())
				return e;
		}
		return null;
	}
	
	
	
	private void secondPath(){
		graph = routeFinder.getGraph();
		e = getFirstCongestedEdge(path);
		graph.removeEdge(e);
		secondPath = routeFinder.getSecondRoute(graph, startNode, endNode);
	}
	
	
	
	private boolean checkStart(){
		double tickCount = 210.0; //get the current system tick and pass it into this.
		double probability = routeFinder.getProbability(tickCount); 
		if(Math.random() <= probability) 
			return true;
		else 
			return false;
	}
	
	
		
	private void reset(){
		stage=0;
		progress = 0;
		active = false;
		startNode = endNode;
		endNode = nodeSelector.getNode();
		
		//Try to avoid having the same start and end node (not crucial).
		endNode = (startNode == endNode) ? nodeSelector.getNode() : endNode;
		
		path = routeFinder.getRoute(startNode, endNode);
		if(checkCongestion(path)){
			secondPath();
		}
	}
	
	public boolean getStatus(){
		return active;
	}
	
	
		
	/*	@Watch(watcheeClassName = "aClassName",
			watcheeFieldNames = "aFieldName",
			query = "aQuery",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void watch(){
		//TODO logic executed only when something happens.

	}*/

}
