package finalProject;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 
 * @since       1.0          
 */

public class Agent {
	
	//The agent class contains the logic for standard agent behaviour. 

	/**
	 * Stores the memory location of the nodeSelector object.
	 */
	NodeSelector nodeSelector;
	
	/**
	 * Stores the memory location of the routeFinder object.
	 */
	RouteFinder routeFinder;
	
	/**
	 * Stores the memory location of the supervisor object.
	 */
	Supervisor supervisor;
	
	/**
	 * Stores the node selected as the start node of the current journey.
	 */
	MyNode startNode;
	
	/**
	 * Stores the node selected as the terminal node of the current journey.
	 */	
	MyNode endNode;
	
	/**
	 * Stores the edge that the agent is currently using.
	 */	
	MyEdge e;						
	
	/**
	 * Stores the current optimal path.
	 */	
	List<MyEdge> path;				
	
	/**
	 * Tracks the agents stage through its path (between 0 and pathSize-1).
	 */	
	int stage; 						
	
	/**
	 * Tracks the progress along the current edge (between 0%-100%).
	 */
	double progress;				
	
	/**
	 * True if the agent is currently on a route.
	 */
	boolean active;	
	
	/**
	 * Total number of system ticks so far in the current journey. Used for data reporting.
	 */	
	int journeyLength;				

	
	//Empty constructor required because learningAgent implements this class.
	public Agent(){		
	}

	public Agent(NodeSelector nodeSelector, RouteFinder routeFinder, Supervisor supervisor){

		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		this.supervisor = supervisor;
		stage = 0;			
		progress = 0;	
		active = false;
		journeyLength = 0;
		
	//Obtain the first start and end nodes.
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
	//Avoid having the same start node and end node.
		while(startNode == endNode)
			endNode = nodeSelector.getNode();		
	}
		
//------------------Step Method---------------------------------------------------------------------------------------

	/**
	 * Step method called once per system tick. For inactive agents, checks whether to start a new journey. For active 
	 * agents, progresses them along their current journey.
	 */
	@ScheduledMethod(start = 1, interval = 1) 
	public void step(){
				
		//Do nothing unless active.
		if(active==false){
			if(checkStart(supervisor)){
				active = true;
				supervisor.incrementNumAgents();
				path = routeFinder.getShortestRoute(startNode, endNode);
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
			reset(supervisor, nodeSelector);
		}	
	}
		
//------------------Utility Methods---------------------------------------------------------------------------------------
					
	/**
	 * Check whether or not to start a new journey.
	 * @param supervisor is the object that stores the probability that an agent should start a new journey.
	 * @return true if the agent should start a new journey, false otherwise.
	 */
	protected boolean checkStart(Supervisor supervisor){
		double probability = supervisor.getProbability(); 
		if(Math.random() <= probability) 
			return true;
		else 
			return false;
	}
	
	/**
	 * Increase the progress along the current edge.
	 */
	protected void updateProgress(){
		progress = progress + e.getProgressRate();
		journeyLength++;
	}
	
	/**
	 * Reset to initial conditions prior to starting a new route.	
	 * @param supervisor
	 * @param nodeSelector
	 * @param routeFinder
	 */
	protected void reset(Supervisor supervisor, NodeSelector nodeSelector){
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
	
//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	/**
	 * Used for data gathering.
	 * @return whether the agent is active or not. 
	 */
	public boolean getStatus(){
		return active;
	}
	
	/**
	 * Send the length of last completed journey to the supervisor.
	 * @param supervisor
	 */
	protected void publishJourneyLength(Supervisor supervisor){
		supervisor.appendJourneyLength(journeyLength);
	}
		
}
