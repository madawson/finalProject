package finalProject;

import java.util.HashMap;
import java.util.List;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class LearningAgent extends Agent {
	
	private boolean qLearning;
	private boolean wolfPhc;
	private HashMap<Integer, Double[]> qValues;
	private boolean preJourneyStage;
	private int action; 					//0 for proceed, 1 for yield.
	private double reward;
	private int state;
	private double discountFactor; 			//*NOTE: You need to add proper functionality to make this decay intelligently and to choose the decay rate.*
	private int warningReceived; 			//0 if true, 1 if false.
	private int currentStateAction;
	private double cumulativeWeight;
	private double actualJourneyWeight;
	private double estimatedJourneyWeight;
	private int edgeJourneyTime;
	private int[][] stateArray;				//Stores an index for each state.
	private int[][] stateActionArray;		//Stores an index for a state/action pair.
	private int[] stateVisitedCount;		//Stores the number of times a state has been visited.
	private double[][] averagePolicy;
	private double[][] mixedPolicy;
	private List<MyEdge> secondPath;		//Stores the current sub-optimal path.
	private double policyTemperature;
	private double deltaLearningRateWinning;
	private double deltaLearningRateLosing;
	
	//-----Parameters used for data reporting----------------------------------------------------------------
	
	private int totalJourneys;
	private int totalJourneyTime;
	private double averageJourneyTime;
	private int totalLosses;
	
	ScheduleParameters endOfRun = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
	Schedule schedule = new Schedule();
		
	public LearningAgent(NodeSelector nodeSelector, RouteFinder routeFinder, Supervisor supervisor){

	//-----Initialisation steps common to all agents---------------------------------------------------------
		
		stage = 0;			
		progress = 0;
		active = false;
		journeyLength = 0;
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		this.supervisor = supervisor;
		policyTemperature = 0.3;
		
		//Obtain the first start and end nodes.
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
		//Avoid having the same start node and end node.
		while(startNode == endNode)
			endNode = nodeSelector.getNode();
				
	//-----Initialisation steps for learning agents----------------------------------------------------------
		
		//Switch Q-Learning on or off.
		qLearning = true;
		
		//Switch WoLF PHC on or off.
		wolfPhc = false;
		
		//Stores a reward value alongside an associated discount factor.
		qValues = new HashMap<Integer,Double[]>();
		
		stateArray = new int[24][2];		 
		stateActionArray = new int[48][2];	 
		
		totalJourneys = 0;
		preJourneyStage = false;
		action = 0;
		reward = 0.0;
		state = 0;
		discountFactor = 1.0;
		warningReceived = 1;
		cumulativeWeight = 0.0;
		actualJourneyWeight = 0.0;
		estimatedJourneyWeight = 0.0;
		edgeJourneyTime = 0;
		totalJourneyTime = 0;
		averageJourneyTime = 0.0;
		
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
		
		initialiseHashTable(); 
		
		//Schedule methods to run at the end of simulation.
		schedule.schedule(endOfRun, this, "printFinalDecisions");
		
		//-----Initialisation steps WoLF PHC----------------------------------------------------------	
		
		deltaLearningRateWinning = 0.2;
		deltaLearningRateLosing = 1.0;
		
		stateVisitedCount = new int[48];
		averagePolicy = new double[48][2];
		mixedPolicy = new double[48][2];
		
		//Populate the mixed policy array with (1/action_set_size)
		for(int i = 0; i<48; i++){
			for(int j = 0; j<2; j++){
				mixedPolicy[i][j] = 0.5;
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
					secondPath(routeFinder);
					if(checkCongestion(path)){
						warningReceived = 0;
					}
				}
				else 
					return;
			}

			//Learning agents make a decision to follow the optimal or sub-optimal route.
			if(preJourneyStage == true){
				estimatedJourneyWeight = calculateEstimatedJourneyWeight(path);
				if(qLearning){
					//Observe the state and retrieve the appropriate action.
					state = getState();
					action = getAction(state);
					if(action == 1){
						path = secondPath;
					}
				}
				else{
				
				 	double newRandom = Math.random();
					if(newRandom<0.8){
					//	action = 1;
					//	path = secondPath;
						action = 0;
					}
					else{
					//	action = 0;
						action = 1;
						path = secondPath;
					}
				
				//	action = 0; //Used for always proceed
				//	action = 1;
				//	path = secondPath;
				}
				preJourneyStage = false;
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
				warningReceived = 1;
				//Calculate the reward (difference between estimated journey length and actual journey length)
				totalJourneys++;
				totalJourneyTime += journeyLength;
				averageJourneyTime = totalJourneyTime / totalJourneys;
				reward = actualJourneyWeight - estimatedJourneyWeight;
				if(reward>0)
					totalLosses++;
		/*		System.out.println("Proceed = " + action);
				System.out.println("Actual Journey Weight = " + actualJourneyWeight);
				System.out.println("Estimated Journey Weight = " + estimatedJourneyWeight);
				System.out.println("Current State Action Before Update = " + currentStateAction);
				System.out.println("Q-Value BEFORE update = " + qValues.get(currentStateAction)[0]);
				System.out.println("DF BEFORE update = " + qValues.get(currentStateAction)[1]); */
				actualJourneyWeight = 0.0;
				estimatedJourneyWeight = 0.0;
				if(qLearning){
					updateQValue(currentStateAction,reward);
				}
				if(wolfPhc){
					updateAveragePolicy(state);
					updateMixedPolicy(state,action);
				}
		/*		System.out.println("Current State Action After Update = " + currentStateAction);
				System.out.println("Q-Value AFTER update = " + qValues.get(currentStateAction)[0]);
				System.out.println("DF AFTER update = " + qValues.get(currentStateAction)[1]);
				System.out.println(" ");
				System.out.println(" "); */
				reset(supervisor, nodeSelector, routeFinder);
			}
		}
		
//--------------Q-learning methods-----------------------------------------------------------------------------------
		
	/**
	 * Use the value of the current system tick to look up the state number.
	 * @return
	 */
	private int getState(){
		int timeIndex = (int)supervisor.getTimeIndex();
		return stateArray[timeIndex][warningReceived];
	}
	
	/**
	 * Select an action based on the current state.
	 * @param state is the current state number.
	 * @return either an integer 1 or integer 0 corresponding to the actions "takeSubOptimal" and "takeOptimal" respectively.
	 */
	private int getAction(int state){
		
		Integer stateProceed = new Integer(stateActionArray[state][0]);
		Integer stateYield = new Integer(stateActionArray[state][1]);
		double proceedProbability = 0.0;
		if(!qValues.containsKey(stateProceed) & !qValues.containsKey(stateYield)){
			Double[] rewardAndDiscount = new Double[2];
			rewardAndDiscount[0] = new Double(0.0);
			rewardAndDiscount[1] = new Double(discountFactor);
			qValues.put(stateProceed, rewardAndDiscount);
			qValues.put(stateYield, rewardAndDiscount);
			currentStateAction = stateYield;
	/*		System.out.println("State Number = " + state);
			System.out.println("Current StateAction Number = " + currentStateAction);
			System.out.println("New Entry Created"); */
			return 1;
		}
		else{
			if(!wolfPhc){
				proceedProbability = getSoftMaxProbability(stateProceed, stateYield);
			}
			else{
				proceedProbability = getMixedProbabilityYield(state);
			}
			
			if(Math.random() <= proceedProbability){
				currentStateAction = stateYield;
				return 1;
			}
			else{
				currentStateAction = stateProceed;
				return 0;
			} 
		}
	}
	
	/**
	 * Update the stored Q-value and associated discount factor.
	 * @param stateAction is the index of the current state-action pair.
	 * @param reward is latest reward that has been obtained.
	 */
	private void updateQValue(int stateAction, double reward){
		double oldReward = qValues.get(stateAction)[0];
		double stateDiscountFactor = qValues.get(stateAction)[1];
		Double[] rewardAndDiscount = new Double[2];
		double newReward = oldReward + (stateDiscountFactor*(reward - oldReward));
		//Code to make the learning rate decay
		
		/*
		if(stateDiscountFactor != 0.0){
			stateDiscountFactor -= 0.1;
		}
		*/
		rewardAndDiscount[0] = newReward;
		rewardAndDiscount[1] = stateDiscountFactor;
		qValues.put(stateAction, rewardAndDiscount);
/*		System.out.println("Q-Value Updated for state action = " + stateAction);
		System.out.println("Reward Received = " + reward);
		System.out.println("Old Reward = " + oldReward);
		System.out.println("New Reward = " + newReward);
		System.out.println("New Discount Factor = " + stateDiscountFactor); */
	}
	
	//-----WoLF PHC specific methods----------------------------------------------------------------------------------
	
	/**
	 * Update the WoLF PHC average policy.
	 * @param state is the current state number.
	 */
	private void updateAveragePolicy(int state){
		stateVisitedCount[state] += 1; 			//Increment the number of times this state has been visited.
		for(int i = 0; i<2; i++){
			averagePolicy[state][i] += (1/stateVisitedCount[state])*(mixedPolicy[state][i] - averagePolicy[state][i]);
		}
	}
	
	/**
	 * Update the WolF PHC mixed policy.
	 * @param state is the current state number.
	 * @param action is the current action number; 1 for "takeSubOptimal" or 0 for "takeOptimal".
	 */
	private void updateMixedPolicy(int state, int action){
		
		int oppositeAction = 0;
		double currentDelta;
		
		switch (action){
			case 0: oppositeAction = 1; break;
			case 1: oppositeAction = 0; break;
		}
		
		if(winning(state, action, oppositeAction))
			currentDelta = deltaLearningRateWinning;
		else
			currentDelta = deltaLearningRateLosing; 
		
		
		if(stateActionArray[state][action]>stateActionArray[state][oppositeAction])
			currentDelta = (-1)*currentDelta;
		
		mixedPolicy[state][action] += currentDelta;
		
		normaliseProbabilities(state);
	}
	
	/**
	 * Calculates whether the agent is 'winning' or 'losing' in the context of the WoLF PHC algorithm.
	 * @param state is the current state number.
	 * @param action is the current action number; 1 for "takeSubOptimal" or 0 for "takeOptimal".
	 * @param oppositeAction is the opposite value to the current action number.
	 * @return
	 */
	private boolean winning(int state, int action, int oppositeAction){
		double mixedPolicySum = 0.0;
		double averagePolicySum = 0.0;
		
		for(int i = 0; i<2; i++){
			int stateAction = stateActionArray[state][action];
			mixedPolicySum += mixedPolicy[state][action]*qValues.get(stateAction)[0];
			stateAction = stateActionArray[state][oppositeAction];
			mixedPolicySum += mixedPolicy[state][oppositeAction]*qValues.get(stateAction)[0];
		}
		
		for(int i = 0; i<2; i++){
			int stateAction = stateActionArray[state][action];
			averagePolicySum += averagePolicy[state][action]*qValues.get(stateAction)[0];
			stateAction = stateActionArray[state][oppositeAction];
			averagePolicySum += averagePolicy[state][oppositeAction]*qValues.get(stateAction)[0];
		}
		
		if(mixedPolicySum < averagePolicySum)
			return true;
		else
			return false;
	}
	
	/**
	 * Constrains the newly calculated probabilities to a legal probability distribution.
	 * @param state is the current state number.
	 */
	private void normaliseProbabilities(int state){
		double takeOptimalProbability = mixedPolicy[state][0];
		double takeSubOptimalProbability = mixedPolicy[state][1];
		double total = takeOptimalProbability + takeSubOptimalProbability;
		
		mixedPolicy[state][0] = takeOptimalProbability/total;
		mixedPolicy[state][1] = takeSubOptimalProbability/total;
	}
	
	//--------------Utility methods-----------------------------------------------------------------------------------
	
	/**
	 * Calculates the total journey weight for a path.
	 * @param path is the sequence of edges constituting a route.
	 * @return the total weight of all edges contained within the path.
	 */
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
	
	/**
	 * Calculate a sub-optimal route.	
	 * @param routeFinder is the object that contains the functionality to calculate routes.
	 */
	protected void secondPath(RouteFinder routeFinder){
		MyEdge e = getFirstCongestedEdge(path);
		secondPath = routeFinder.getSecondRoute(e, startNode, endNode);
		if(secondPath.isEmpty() == true){
			secondPath = path;
			return;
		}
	}
	
	/**
	 * Check for at least one congested edge on the calculated route.
	 * @param path is the sequence of edges constituting a route. 
	 * @return
	 */
	protected boolean checkCongestion(List<MyEdge> path){
		MyEdge e = path.get(0);
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			if(e.getNumUsers() >= e.getThreshold())
				return true;
		}
		return false;
	}
	
	/**
	 * Finds the first congested edge within a path.
	 * @param path
	 * @return
	 */
	protected MyEdge getFirstCongestedEdge(List<MyEdge> path){
		int greatestNumUsers = 0;
		int currentNumUsers = 0;
		MyEdge mostCongestedEdge = path.get(0);
		for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			currentNumUsers = e.getNumUsers();
			if(currentNumUsers > greatestNumUsers){
				greatestNumUsers = currentNumUsers;
				mostCongestedEdge = e;
			}
		}
		return mostCongestedEdge;
	}
	
	/**
	 * Calculates an exploration probability using the Softmax approach.
	 * @param stateProceed is the index of the tuple that represents the current state paired with the "takeOptimal" action.
	 * @param stateYield is the index of the tuple that represents the current state paired with the "takeSubOptimal" action.
	 * @return
	 */
	protected double getSoftMaxProbability(Integer stateAction1, Integer stateAction2){
		double action1Term = Math.exp(qValues.get(stateAction1)[0]/policyTemperature);
		double action2Term = Math.exp(qValues.get(stateAction2)[0]/policyTemperature);
		double total = action1Term + action2Term;
		return action1Term/total;
	}
	
	protected double getMixedProbabilityYield(int state){
		return mixedPolicy[state][1];
	}
	
	protected void initialiseHashTable(){
		int currentState;
		Integer stateProceed;
		Integer stateYield;
		Double[] rewardAndDiscount;
		for(int i = 0; i<24; i++){
			for(int j = 0; j<2; j++){
				currentState = stateArray[i][j];
				stateProceed = new Integer(stateActionArray[currentState][0]);
				stateYield = new Integer(stateActionArray[currentState][1]);
				rewardAndDiscount = new Double[2];
				rewardAndDiscount[0] = new Double(0.0);
				rewardAndDiscount[1] = new Double(discountFactor);
				qValues.put(stateProceed, rewardAndDiscount);
				qValues.put(stateYield, rewardAndDiscount);
			}
		}
		
	}
	
	//------------------Data Gathering Methods----------------------------------------------------------------------------
	
	/**
	 * Divides the total number of losses by the total number of journeys.
	 * @return total number of losses divided by total number of journeys.
	 */
	public int getWinningOrLosing(){
		if(totalJourneys == 0)
			return 0;
		else
			return totalLosses/totalJourneys;
	}
	
	/**
	 * Used for data gathering.
	 * @return total number of journeys. 
	 */
	public int getTotalJourneys(){
		return totalJourneys;
	}
	
	/**
	 * Used for data gathering. 
	 * @return  total number of losses.
	 */
	public int getTotalLosses(){
		return totalLosses;
	}
	
	/**
	 * Used for data gathering.
	 * @return average journey time of this learning agent.
	 */
	public double getAverageJourneyTime(){
		return averageJourneyTime;
	}
	
	public void printFinalDecisions(){
		int takeOptimal = 0;
		int takeSubOptimal = 0;
		for(int i = 0; i<48; i++){
			if(stateActionArray[i][0] < stateActionArray[i][1])
				takeOptimal++;
			else
				takeSubOptimal++;	
		}
		System.out.println("takeOptimal total = " + takeOptimal + " and takeSubOptimal total = " + takeSubOptimal);
	}
	
	@ScheduledMethod(start = 1, interval = 10)
	public void reportProbabilityTakeOptimal(){
		
		/*int state = 36;
		Integer stateProceed = new Integer(stateActionArray[state][0]);
		Integer stateYield = new Integer(stateActionArray[state][1]);
		double probabilityTakeOptimal = getSoftMaxProbability(stateProceed, stateYield);*/
		System.out.println(getState());
		Integer stateProceed = new Integer(stateActionArray[state][0]);
		System.out.println(qValues.get(stateProceed)[0]);
	}

		
}
