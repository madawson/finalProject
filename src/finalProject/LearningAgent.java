package finalProject;

import java.util.HashMap;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

public class LearningAgent extends Agent {
	
	private HashMap<Integer, Double[]> qValues;
	private boolean preJourneyStage;
	private int action; //0 for proceed, 1 for yield.
	private double reward;
	private double discountFactor; //*NOTE: You need to add proper functionality to make this decay intelligently and to choose the decay rate.*
	private int warningReceived; //0 if true, 1 if false.
	private int currentStateAction;
	private double cumulativeWeight;
	private double actualJourneyWeight;
	private double estimatedJourneyWeight;
	private int edgeJourneyTime;
	private int[][] stateArray;
	private int[][] stateActionArray;
		
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
		
	//Obtain a path.
/*		path = this.routeFinder.getRoute(startNode, endNode);
		distance = this.routeFinder.getDistance(startNode, endNode);
		if(checkCongestion(path)){
			warningReceived = 0;
			secondPath(routeFinder);	
		} */
		
//-----Initialisation steps for learning agents----------------------------------------------------------
		
		//Create the (empty) reward table.
		//48 states (24 time zones each with a boolean) and two possible actions.
		//States are 0-23 and Actions are 0-1.		
		qValues = new HashMap<Integer,Double[]>();
		
	//Array that stores a reward value alongside an associated discount factor.
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
					distance = routeFinder.getDistance(startNode, endNode);
					if(checkCongestion(path)){
						warningReceived = 0;
						secondPath(routeFinder);
					}
				}
				else 
					return;
			}

	//Observe the state at the beginning of the journey.
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
			currentStateAction = stateYield;
			System.out.println("State Number = " + state);
			System.out.println("Current StateAction Number = " + currentStateAction);
			return 1;

		}
		else{
			currentStateAction = stateProceed;
			System.out.println("State Number = " + state);
			System.out.println("Current StateAction Number = " + currentStateAction);
			return 0;
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
}
