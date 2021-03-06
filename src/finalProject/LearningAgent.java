package finalProject;

import java.util.ArrayList;
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
	
	/**
	 * True if Q-Learning is activated, false otherwise.
	 */
	boolean qLearning;
	
	/**
	 * True if WoLF-PHC is activated, false otherwise. Note that Q-Learning must be activated for WoLF-PHC to be activated.
	 */
	boolean wolfPhc;
	
	/**
	 * Hash Table used to store the Q-Values and discount factors for each state-action pair.
	 */
	HashMap<Integer, Double[]> qValues;
	
	/**
	 * True if the agent has chosen to start a journey and has not yet completed their pre-journey tasks. False otherwise.
	 */
	private boolean preJourneyStage;
	
	/**
	 * Q-Learning learning rate, denoted by alpha in the literature.
	 */
	double discountFactor; 
	
	/**
	 * True if the agent has received a congestion warning for their current optimal route, false otherwise. 0 if true, 1 if false.
	 */
	private int warningReceived; 			
	
	/**
	 * The last action that was chosen by the agent. 0 for takeOptimal, 1 for takeSubOptimal.
	 */
	private int currentAction;	
	
	/**
	 * The index of the current state-action pair.
	 */
	private int currentStateAction;
	
	private double cumulativeWeight;
	private double actualJourneyWeight;
	private double estimatedJourneyWeight;
	private int edgeJourneyTime;
	private int[][] stateArray;				//Stores an index for each state.
	int[][] stateActionArray;		//Stores an index for a state/action pair.
	int[] stateVisitedCount;		//Stores the number of times a state has been visited.
	double[][] averagePolicy;
	double[][] mixedPolicy;
	List<MyEdge> secondPath;		//Stores the current sub-optimal path.
	private double policyTemperature;
	private double deltaLearningRateWinning;
	private double deltaLearningRateLosing;
	int currentStartState;
	
//-----Parameters used for data reporting----------------------------------------------------------------
	
	private double totalJourneys;
	private double totalJourneyTime;
	private double averageJourneyTime;
	private double totalLosses;
	Integer state14Proceed;
	Integer state14Yield;
	double state14Prob;
	int numTimes14;
	
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
		
		currentAction = 0;
		totalJourneys = 0;
		preJourneyStage = false;
		discountFactor = 0.1;
		warningReceived = 1;
		cumulativeWeight = 0.0;
		actualJourneyWeight = 0.0;
		estimatedJourneyWeight = 0.0;
		edgeJourneyTime = 0;
		totalJourneyTime = 0.0;
		averageJourneyTime = 0.0;
		policyTemperature = 25.0;
		
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
		
		state14Proceed = new Integer(stateActionArray[32][0]);
		state14Yield = new Integer(stateActionArray[32][1]);
		state14Prob = 0.0;
		
		//Schedule methods to run at the end of simulation.
		//schedule.schedule(endOfRun, this, "printFinalDecisions");
		
//-----Initialisation steps WoLF PHC----------------------------------------------------------	
		
		deltaLearningRateWinning = 0.1;
		deltaLearningRateLosing = 0.4;
		
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
					path = routeFinder.getShortestRoute(startNode, endNode);
					secondPath(routeFinder);
					if(checkCongestion(path)){
						warningReceived = 0;
					}
				}
				else 
					return;
			}

			//PRE-JOURNEY STAGE: Learning agents make a decision to follow the optimal or sub-optimal route.
			if(preJourneyStage == true){
				estimatedJourneyWeight = calculateEstimatedJourneyWeight(path);

				if(qLearning){
					//Observe the current state.
					currentStartState = getState();
					//System.out.println(currentState);
					if(currentStartState == 32){
						double latestSoftMax14 = getSoftMaxProbability(state14Proceed, state14Yield); 
						state14Prob = latestSoftMax14;
						System.out.println(state14Prob);
						//System.out.println(state14Prob + " , " + supervisor.getTimeIndex());
					}

					//Retrieve the appropriate action.
					currentAction = getAction(currentStartState);
					if(currentAction == 1){
						path = secondPath;
					}
				}
				else{
				
				 	double newRandom = Math.random();
					if(newRandom<1.0){
					//	action = 1;
					//	path = secondPath;
						currentAction = 0;
					}
					else{
					//	action = 0;
						currentAction = 1;
						path = secondPath;
					}
				
				//	action = 0; //Used for always proceed
				//	action = 1;
				//	path = secondPath;
				}
				
				preJourneyStage = false;
			}
			//End of pre-journey stage.
			
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
				
				//Calculate the reward (difference between estimated optimal journey length and actual journey length).

				totalJourneys++;

				totalJourneyTime += journeyLength;
				averageJourneyTime = totalJourneyTime / totalJourneys;
				
				//Calculate the reward (difference between estimated optimal journey length and actual journey length).
				double currentReward = actualJourneyWeight - estimatedJourneyWeight;

					if(currentReward>=0){
						totalLosses++;
					}

				
				actualJourneyWeight = 0.0;
				estimatedJourneyWeight = 0.0;
				
				if(qLearning){

						//System.out.println(getTotalLosses()/getTotalJourneys());
						//System.out.println(averageJourneyTime);

					updateQValue(currentStateAction,currentReward);	
				}
				if(wolfPhc){
					updateAveragePolicy(currentStartState);
					updateMixedPolicy(currentStartState,currentAction);
				}
				
				//System.out.println(getTotalLosses()/getTotalJourneys());
				reset(supervisor, nodeSelector);
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
	 * Select an action based on the current state. Obtains the probability that the agent should proceed and then draws from
	 * that probability distribution. 
	 * @param state is the current state number.
	 * @return either an integer 0 or integer 1 corresponding to the actions "takeOptimal" and "takeSubOptimal" respectively.
	 */
	private int getAction(int state){
		
		Integer stateProceed = new Integer(stateActionArray[state][0]);
		Integer stateYield = new Integer(stateActionArray[state][1]);
		double proceedProbability = 0.0;
		
		
		if(!wolfPhc){
			proceedProbability = getSoftMaxProbability(stateProceed, stateYield);
		}
		else{
			proceedProbability = getMixedProbabilityYield(state);
		}
		
			
		if(Math.random() <= proceedProbability){
			currentStateAction = stateProceed;
			return 0;
		}
		else{
			currentStateAction = stateYield;
			return 1;
		} 
	}
	
	/**
	 * Update the stored Q-value and associated discount factor.
	 * @param stateAction is the index of the current state-action pair.
	 * @param reward is latest reward that has been obtained.
	 */
	 void updateQValue(int stateAction, double reward){
		double oldReward = qValues.get(stateAction)[0];
		double stateDiscountFactor = qValues.get(stateAction)[1];
		double newReward = oldReward + (stateDiscountFactor*(reward - oldReward));
		
		//Code to make the learning rate decay		
		/*
		if(stateDiscountFactor != 0.0){
			stateDiscountFactor -= 0.1;
		}
		*/
		
		Double[] rewardAndDiscount = new Double[2];
		rewardAndDiscount[0] = newReward;
		rewardAndDiscount[1] = stateDiscountFactor;
		qValues.put(stateAction, rewardAndDiscount);
	}
	
//-----WoLF PHC specific methods----------------------------------------------------------------------------------
	
	/**
	 * Update the WoLF PHC average policy.
	 * @param state is the current state number.
	 */
	void updateAveragePolicy(int state){
		stateVisitedCount[state] += 1; 			//Increment the number of times this state has been visited.
		for(int i = 0; i<2; i++){
			double stepChange = (1.0/stateVisitedCount[state])*(mixedPolicy[state][i] - averagePolicy[state][i]);		
			averagePolicy[state][i] += stepChange;
		}
	}
	
	/**
	 * Update the WolF PHC mixed policy.
	 * @param state is the current state number.
	 * @param action is the current action number; 1 for "takeSubOptimal" or 0 for "takeOptimal".
	 */
	void updateMixedPolicy(int state, int action){
		
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
		
		
		if(qValues.get(stateActionArray[state][action])[0] > qValues.get(stateActionArray[state][oppositeAction])[0]){
			currentDelta = (-1)*currentDelta;
		}
		
		mixedPolicy[state][action] += currentDelta;
		
		if(mixedPolicy[state][action] < 0.0){
			mixedPolicy[state][action] = 0.0;
		}
		else if(mixedPolicy[state][action] > 1.0){
			mixedPolicy[state][action] = 1.0;
		}
		
		normaliseProbabilities(state);
	}
	
	/**
	 * Calculates whether the agent is 'winning' or 'losing' in the context of the WoLF PHC algorithm.
	 * @param state is the current state number.
	 * @param action is the current action number; 1 for "takeSubOptimal" or 0 for "takeOptimal".
	 * @param oppositeAction is the opposite value to the current action number.
	 * @return
	 */
	boolean winning(int state, int action, int oppositeAction){
		double mixedPolicySum = 0.0;
		double averagePolicySum = 0.0;
		
		
			int stateAction = stateActionArray[state][action];
			mixedPolicySum += mixedPolicy[state][action]*qValues.get(stateAction)[0];
			stateAction = stateActionArray[state][oppositeAction];
			mixedPolicySum += mixedPolicy[state][oppositeAction]*qValues.get(stateAction)[0];
		
		
		
			stateAction = stateActionArray[state][action];
			averagePolicySum += averagePolicy[state][action]*qValues.get(stateAction)[0];
			stateAction = stateActionArray[state][oppositeAction];
			averagePolicySum += averagePolicy[state][oppositeAction]*qValues.get(stateAction)[0];
		
		
		if(mixedPolicySum < averagePolicySum) //less than because rewards are negative.
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
	double calculateEstimatedJourneyWeight(List<MyEdge> path){
		
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
	 * Used to obtain a second-shortest route.	
	 * @param routeFinder is the object that contains the functionality to calculate routes.
	 */
	void secondPath(RouteFinder routeFinder){
		List<MyEdge> currentPath = new ArrayList<MyEdge>();
		double currentPathLength = 0.0;
		double shortestLength = 100000.0;
		for(int i = 0; i<path.size(); i++){
			MyEdge e = path.get(i);
			currentPath = routeFinder.getSubOptimalRoute(e, startNode, endNode);
			if(currentPath.isEmpty() == true){
				continue;
			}
			else{
				currentPathLength = routeFinder.getPathLength(currentPath);
			}
				
			if(currentPathLength < shortestLength){
				secondPath = currentPath;
				shortestLength = currentPathLength;
			}
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
	 * Calculates an exploration probability using the Softmax approach.
	 * @param stateProceed is the index of the tuple that represents the current state paired with the "takeOptimal" action.
	 * @param stateYield is the index of the tuple that represents the current state paired with the "takeSubOptimal" action.
	 * @return
	 */
	double getSoftMaxProbability(Integer stateAction1, Integer stateAction2){
		double action1Term = Math.exp(qValues.get(stateAction1)[0]/policyTemperature);
		double action2Term = Math.exp(qValues.get(stateAction2)[0]/policyTemperature);
		double total = action1Term + action2Term;
		return 1-(action1Term/total);
	}
	
	protected double getMixedProbabilityYield(int state){
		return mixedPolicy[state][1];
	}
	
	/**
	 * Initialises the Hash Table used to store Q-Values and discount factors for each state-action pair. The initial discount
	 * factor is configurable, and the initial Q-Values are all set to 0.0.
	 */
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
	public double getWinningOrLosing(){
		if(totalJourneys == 0)
			return 0;
		else
			return totalLosses/totalJourneys;
	}
	
	/**
	 * Used for data gathering.
	 * @return total number of journeys. 
	 */
	public double getTotalJourneys(){
		return totalJourneys;
	}
	
	/**
	 * Used for data gathering. 
	 * @return  total number of losses.
	 */
	public double getTotalLosses(){
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
		
}
