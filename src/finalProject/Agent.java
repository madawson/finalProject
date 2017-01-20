package finalProject;

import java.util.List;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import repast.simphony.engine.schedule.ScheduledMethod;


public class Agent {
	
	//Utility variables
	NodeSelector nodeSelector;
	RouteFinder routeFinder;
	Supervisor supervisor;
	MyNode startNode;
	MyNode endNode;
	List<MyEdge> path;
	List<MyEdge> secondPath;
	
	//Stores the current edge.
	MyEdge e;
		
	//Tracks the agents stage through its path.
	int stage;
		
	//Tracks the progress along the current edge.
	double progress;
		
	//True if the agent is currently on a route.
	boolean active;
		
	//Empty constructor required because learningAgent implements this class.
	public Agent(){		
	}

	//Agent constructor.
	public Agent(NodeSelector nodeSelector, RouteFinder routeFinder, Supervisor supervisor){
		
		//Initialise the agent instance variables.
		stage = 0;			
		progress = 0;		
		active = false;
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		this.supervisor = supervisor;
		
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
			if(checkStart()){
				active = true;
				supervisor.incrementNumAgents();
			}
			else 
				return;
		}
		
		if(path.size() > 0 & stage < path.size()){

				if(progress == 0){
					e = path.get(stage);
					e.joinEdge();
				}
				
				updateProgress();
				
				if(progress >= 100){
					e.leaveEdge();
					stage++;
					progress=0;
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
	

	protected boolean checkCongestion(List<MyEdge> path){
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getThreshold() >= e.getCapacity())
				return true;
		}
		return false;
	}
	
	
	
	protected MyEdge getFirstCongestedEdge(List<MyEdge> path){
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getThreshold() >= e.getCapacity())
				return e;
		}
		return null;
	}
	
	
	
	protected void secondPath(){
		DirectedSparseMultigraph<MyNode,MyEdge> graph = routeFinder.getGraph();
		e = getFirstCongestedEdge(path);
		graph.removeEdge(e);
		secondPath = routeFinder.getSecondRoute(graph, startNode, endNode);
	}
	
	
	
	protected boolean checkStart(){
		double probability = supervisor.getProbability(); 
		if(Math.random() <= probability) 
			return true;
		else 
			return false;
	}
	
	protected void updateProgress(){
		double weight;
		//Progress is a function of edge weight.
		weight = e.getWeight();
		progress = progress + ((-0.1*weight) + 11);
	}
	
		
	protected void reset(){
		stage = 0;
		progress = 0;
		active = false;
		supervisor.decrementNumAgents();
		startNode = endNode;
		endNode = nodeSelector.getNode();
		
		//Try to avoid having the same start and end node (not crucial).
		endNode = (startNode == endNode) ? nodeSelector.getNode() : endNode;
		
		path = routeFinder.getRoute(startNode, endNode);
		if(checkCongestion(path)){
			secondPath();
		}
	}
	
	//Required for data gathering.
	public boolean getStatus(){
		return active;
	}
	
}
