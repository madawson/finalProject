package finalProject;

import java.util.HashMap;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

public class LearningAgent extends Agent {
	
	private HashMap<Integer, Double[]> qValues;
	private boolean preJourneyStage;
	private int action; 					//0 for proceed, 1 for yield.
	private double reward;
	private double discountFactor; 			//*NOTE: You need to add proper functionality to make this decay intelligently and to choose the decay rate.*
	private int warningReceived; 			//0 if true, 1 if false.
	private int currentStateAction;
	private double cumulativeWeight;
	private double actualJourneyWeight;
	private double estimatedJourneyWeight;
	private int edgeJourneyTime;
	private int[][] stateArray;				//Stores an index for each state.
	private int[][] stateActionArray;		//Stores an index for a state/action pair.
	private List<MyEdge> secondPath;		//Stores the current sub-optimal path.
	
	//-----Parameters used for data reporting----------------------------------------------------------------
		
	public LearningAgent(NodeSelector nodeSelector, RouteFinder routeFinder, Supervisor supervisor){

	//-----Initialisation steps common to all agents---------------------------------------------------------
		
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
				
	//-----Initialisation steps for learning agents----------------------------------------------------------
		
		//Stores a reward value alongside an associated discount factor.
		qValues = new HashMap<Integer,Double[]>();
		
		stateArray = new int[24][2];		 
		stateActionArray = new int[48][2];	 
		
		preJourneyStage = false;
		action = 0;
		reward = 0.0;
		discountFactor = 0.6;
		warningReceived = 1;
		cumulativeWeight = 0.0;
		actualJourneyWeight = 0.0;
		estimatedJourneyWeight = 0.0;
		edgeJourneyTime = 0;
		
		//Populate the state array
		for(int i = 0; i<24; i++){
			for(int j = 0; j<2; j++){
				stateArray[i][j] = (2*i) + j;
			}
		}
	
		//Populate the state action array
		for(int i = 0; i<48; i++){
			for(int j = 0; j<2; j++){
				stateActionArray[i][j] = (2*i) + j;
			}
		}	
		
	}
	
//------------------Step Method---------------------------------------------------------------------------------------
	
		@ScheduledMethod(start = 1, interval = 1) 
		public void step(){
									
			//Do nothing unless active.
			if(active==false){
				if(checkStart(supervisor)){
					active = true;
					preJourneyStage = true;
					supervisor.incrementNumAgents();	
					path = routeFinder.getRoute(startNode, endNode);
					if(checkCongestion(path)){
						warningReceived = 0;
						secondPath(routeFinder);
					}
				}
				else 
					return;
			}

			//Learning agents make a decision to follow the optimal or sub-optimal route.
			if(preJourneyStage == true){
				//Observe the state and retrieve the appropriate action.
				action = getAction(getState());
				if(action == 1){
					path = secondPath;
				}
				preJourneyStage = false;
				warningReceived = 1;
				estimatedJourneyWeight = calculateEstimatedJourneyWeight(path);
			}
			
			//Move along a route.
			if(path.size() > 0 & stage < path.size()){

					if(progress == 0){
						e = path.get(stage);
						e.joinEdge();
					}
					
					updateProgress();
					edgeJourneyTime += 1;
					cumulativeWeight += e.getWeight();
					
					if(progress >= 100){
						e.leaveEdge();
						stage++;
						progress=0;
						actualJourneyWeight += (cumulativeWeight/edgeJourneyTime);
						cumulativeWeight = 0.0;
						edgeJourneyTime = 0;
					}
				
			}
			else {
				//Calculate the reward (difference between estimated journey length and actual journey length)
				reward = actualJourneyWeight - estimatedJourneyWeight;
				System.out.println("Proceed = " + action);
				System.out.println("Actual Journey Weight = " + actualJourneyWeight);
				System.out.println("Estimated Journey Weight = " + estimatedJourneyWeight);
				System.out.println("Current State Action Before Update = " + currentStateAction);
				System.out.println("Q-Value BEFORE update = " + qValues.get(currentStateAction)[0]);
				System.out.println("DF BEFORE update = " + qValues.get(currentStateAction)[1]);
				actualJourneyWeight = 0.0;
				estimatedJourneyWeight = 0.0;
				updateQValue(currentStateAction,reward);
				System.out.println("Current State Action After Update = " + currentStateAction);
				System.out.println("Q-Value AFTER update = " + qValues.get(currentStateAction)[0]);
				System.out.println("DF AFTER update = " + qValues.get(currentStateAction)[1]);
				System.out.println(" ");
				System.out.println(" ");
				reset(supervisor, nodeSelector, routeFinder);
			}
		}
		
//--------------Q-learning methods-----------------------------------------------------------------------------------
		
	//Observe the current state.
	private int getState(){
		int timeIndex = (int)supervisor.getTimeIndex();
		return stateArray[timeIndex][warningReceived];
	}
	
	//Select an action based on the current state.
	private int getAction(int state){
		
		Integer stateProceed = new Integer(stateActionArray[state][0]);
		Integer stateYield = new Integer(stateActionArray[state][1]);
		if(!qValues.containsKey(stateProceed) & !qValues.containsKey(stateYield)){
			Double[] rewardAndDiscount = new Double[2];
			rewardAndDiscount[0] = new Double(0.0);
			rewardAndDiscount[1] = new Double(discountFactor);
			qValues.put(stateProceed, rewardAndDiscount);
			qValues.put(stateYield, rewardAndDiscount);
			currentStateAction = stateProceed;
			System.out.println("State Number = " + state);
			System.out.println("Current StateAction Number = " + currentStateAction);
			System.out.println("New Entry Created");
			return 0;
		}
		else if(qValues.get(stateProceed)[0] > qValues.get(stateYield)[0]){
			double difference = qValues.get(stateProceed)[0] - qValues.get(stateYield)[0];
			double probability = Math.tanh(difference/100) + 0.05;
			if(Math.random() <= probability){
				currentStateAction = stateYield;
				return 1;
			}
			else{
				currentStateAction = stateProceed;
				System.out.println("Your probability just got trumped!");
				return 0;
			}
		}
		else{
			double difference = qValues.get(stateYield)[0] - qValues.get(stateProceed)[0];
			double probability = Math.tanh(difference/100) + 0.05;
			if(Math.random() <= probability){
				currentStateAction = stateProceed;
				return 0;
			}
			else{
				currentStateAction = stateYield;
				System.out.println("Your probability just got trumped!");
				return 1;
			}
		}
	}
	
	//Update the stored Q-value and associated discount factor.
	private void updateQValue(int stateAction, double reward){
		double oldReward = qValues.get(stateAction)[0];
		double stateDiscountFactor = qValues.get(stateAction)[1];
		Double[] rewardAndDiscount = new Double[2];
		double newReward = oldReward + (stateDiscountFactor*(reward - oldReward));
		if(stateDiscountFactor != 0.0){
			stateDiscountFactor -= 0.1;
		}
		rewardAndDiscount[0] = newReward;
		rewardAndDiscount[1] = stateDiscountFactor;
		qValues.put(stateAction, rewardAndDiscount);
		System.out.println("Q-Value Updated for state action = " + stateAction);
		System.out.println("Reward Received = " + reward);
		System.out.println("Old Reward = " + oldReward);
		System.out.println("New Reward = " + newReward);
		System.out.println("New Discount Factor = " + stateDiscountFactor);
	}
	
	//-----WoLF PHC specific methods----------------------------------------------------------------------------------
	
	//--------------Utility methods-----------------------------------------------------------------------------------
	
	private double calculateEstimatedJourneyWeight(List<MyEdge> path){
		
		double weight = 0.0;
		MyEdge e;

		if(path.size() > 0){
			for(int stage = 0; stage<path.size(); stage++){
					e = path.get(stage);
					weight += e.getWeight();
			}
			return weight;
		}
		else {
			return 0;
		}
		
	}	
	
	//Calculate a sub-optimal route.	
	protected void secondPath(RouteFinder routeFinder){
		e = getFirstCongestedEdge(path);
		secondPath = routeFinder.getSecondRoute(e, startNode, endNode);
		if(secondPath.isEmpty() == true){
			secondPath = path;
			return;
		}
	}
	
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
}
