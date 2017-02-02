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
	int journeyLength;
	double distance;
	double secondDistance;
	
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
		journeyLength = 0;
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		this.supervisor = supervisor;
		
	//Obtain the first start and end nodes.
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
	//Try to avoid having the same start and end node (not crucial).
		endNode = (startNode == endNode) ? nodeSelector.getNode() : endNode;
		
	//Obtain a path.
		path = this.routeFinder.getRoute(startNode, endNode);
		distance = this.routeFinder.getDistance(startNode, endNode);
		if(checkCongestion(path)){
			//secondPath();	
		}
	}
	
	
	
//------------------Step Method---------------------------------------------------------------------------------------
	
	@ScheduledMethod(start = 1, interval = 1) 
	public void step(){
		
//Do nothing unless active.
		if(active==false){
			if(checkStart()){
				active = true;
				supervisor.incrementNumAgents();
			}
			else 
				return;
		}

//Move along a route.
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
			if(checkCongestion(path)){
				//secondPath();
			}
		}
		
	}
	

	
//------------------Utility Methods---------------------------------------------------------------------------------------
	
//Check for at least one congested edge on the calculated route.
	protected boolean checkCongestion(List<MyEdge> path){
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getNumUsers() >= e.getThreshold())
				return true;
		}
		return false;
	}
	
	
	
	protected MyEdge getFirstCongestedEdge(List<MyEdge> path){
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getNumUsers() >= e.getThreshold())
				return e;
		}
		return null;
	}
	
	
//Calculate a sub-optimal path.	
	protected void secondPath(){
		DirectedSparseMultigraph<MyNode,MyEdge> graph = routeFinder.getGraph();
		e = getFirstCongestedEdge(path);
		graph.removeEdge(e);
		secondPath = routeFinder.getSecondRoute(graph, startNode, endNode);
		secondDistance = routeFinder.getSecondDistance(graph, startNode, endNode);
		System.out.println("I've calculated a second path! " + startNode.getId() + " to " + endNode.getId() + " distance = " + secondDistance);
	}
	
	
//Check whether or not to start a new journey.
	protected boolean checkStart(){
		double probability = supervisor.getProbability(); 
		if(Math.random() <= probability) 
			return true;
		else 
			return false;
	}
	
//Increase the progress along the current edge.
	protected void updateProgress(){
		progress = progress + e.getProgressRate();
		journeyLength++;
	}
	
//Reset to conditions prior to starting a new route.	
	protected void reset(){
		stage = 0;
		progress = 0;
		active = false;
		publishJourneyLength();
		journeyLength = 0;
		supervisor.decrementNumAgents();
		startNode = endNode;
		endNode = nodeSelector.getNode();
		
		//Try to avoid having the same start and end node (not crucial).
		endNode = (startNode == endNode) ? nodeSelector.getNode() : endNode;
		
		path = routeFinder.getRoute(startNode, endNode);
		distance = this.routeFinder.getDistance(startNode, endNode);
	}

//Send the length of last completed journey to the supervisor.
	protected void publishJourneyLength(){
		supervisor.appendJourneyLength(journeyLength);
	}
	
//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	public boolean getStatus(){
		return active;
	}
	
}
