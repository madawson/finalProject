package finalProject;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

public class Agent {
	
	//The agent class contains the logic for standard agent behaviour. 
	
	NodeSelector nodeSelector;
	RouteFinder routeFinder;
	Supervisor supervisor;
	MyNode startNode;
	MyNode endNode;
	MyEdge e;						//Stores the current edge.
	List<MyEdge> path;				//Stores the current optimal path.
	int journeyLength;				//Used by the supervisor class for data reporting.
	int stage; 						//Tracks the agents stage through its path.
	double progress;				//Tracks the progress along the current edge.
	boolean active;					//True if the agent is currently on a route.

	
	//Empty constructor required because learningAgent implements this class.
	public Agent(){		
	}

	public Agent(NodeSelector nodeSelector, RouteFinder routeFinder, Supervisor supervisor){
		
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
		
	//Avoid having the same start node and end node.
		while(startNode == endNode)
			endNode = nodeSelector.getNode();		
	}
		
//------------------Step Method---------------------------------------------------------------------------------------
	
	@ScheduledMethod(start = 1, interval = 1) 
	public void step(){
				
		//Do nothing unless active.
		if(active==false){
			if(checkStart(supervisor)){
				active = true;
				supervisor.incrementNumAgents();
				path = routeFinder.getRoute(startNode, endNode);
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
			reset(supervisor, nodeSelector, routeFinder);
		}	
	}
		
//------------------Utility Methods---------------------------------------------------------------------------------------
					
	//Check whether or not to start a new journey.
	protected boolean checkStart(Supervisor supervisor){
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
	
	//Reset to initial conditions prior to starting a new route.	
	protected void reset(Supervisor supervisor, NodeSelector nodeSelector, RouteFinder routeFinder){
		stage = 0;
		progress = 0;
		active = false;
		publishJourneyLength(supervisor);
		journeyLength = 0;
		supervisor.decrementNumAgents();
		startNode = endNode;
		endNode = nodeSelector.getNode();
		
		//Avoid having the same start node and end node.
		while(startNode == endNode)
			endNode = nodeSelector.getNode();
	}

	//Send the length of last completed journey to the supervisor.
	protected void publishJourneyLength(Supervisor supervisor){
		supervisor.appendJourneyLength(journeyLength);
	}
	
//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	public boolean getStatus(){
		return active;
	}
	
}
