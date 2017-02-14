package finalProject;

import java.util.HashMap;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

public class LearningAgent extends Agent {
	
	private HashMap<StateAction, Double[]> qValues;
	private boolean preJourneyStage;
	private boolean action;
	private double reward;
	private double discountFactor; //*NOTE: You need to add proper functionality to make this decay intelligently and to choose the decay rate.*
	private boolean warningReceived;
	private StateAction currentStateAction;
	private Double[] rewardAndDiscount;
	private double cumulativeWeight;
	private double actualJourneyWeight;
	private double estimatedJourneyWeight;
	private int edgeJourneyTime;
		
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
		path = this.routeFinder.getRoute(startNode, endNode);
		distance = this.routeFinder.getDistance(startNode, endNode);
		if(checkCongestion(path)){
			warningReceived = true;
			secondPath(routeFinder);	
		}
		
//-----Initialisation steps for learning agents----------------------------------------------------------
		
		//Create the (empty) reward table.
		//48 states (24 time zones each with a boolean) and two possible actions.
		//States are 0-23 and Actions are 0-1.		
		qValues = new HashMap<StateAction,Double[]>();
		
		rewardAndDiscount = new Double[2];
		
		preJourneyStage = false;
		action = true;
		reward = 0.0;
		discountFactor = 0.6;
		warningReceived = false;
		cumulativeWeight = 0.0;
		actualJourneyWeight = 0.0;
		estimatedJourneyWeight = 0.0;
		edgeJourneyTime = 0;
		
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
				}
				else 
					return;
			}

	//Observe the state at the beginning of the journey.
			if(preJourneyStage == true){
				//Observe the state and retrieve the appropriate action.
				action = getAction(getState());
				if(!action){
					path = secondPath;
				}
				preJourneyStage = false;
				warningReceived = false;
				estimatedJourneyWeight = calculateEstimatedJourneyWeight(path);
				System.out.println("Proceed = " + action);
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
				System.out.println("Actual Journey Weight = " + actualJourneyWeight);
				System.out.println("Estimated Journey Weight = " + estimatedJourneyWeight);
				System.out.println("Q-Value BEFORE update = " + qValues.get(currentStateAction)[0]);
				System.out.println("DF BEFORE update = " + qValues.get(currentStateAction)[1]);
				actualJourneyWeight = 0.0;
				estimatedJourneyWeight = 0.0;
				updateQValue(currentStateAction,reward);
				System.out.println("Q-Value AFTER update = " + qValues.get(currentStateAction)[0]);
				System.out.println("DF AFTER update = " + qValues.get(currentStateAction)[1]);
				reset(supervisor, nodeSelector, routeFinder);
				if(checkCongestion(path)){
					warningReceived = true;
					secondPath(routeFinder);
				}
			}
		}
		
//--------------Q-learning methods-----------------------------------------------------------------------------------
		
	//Observe the current state.
	private State getState(){
		double timeIndex = supervisor.getTimeIndex();
		State state = new State(timeIndex,warningReceived);
		return state;
	}
	
	//Select an action based on the current state.
	private boolean getAction(State state){
		StateAction proceed = new StateAction(state,true);
		StateAction yield = new StateAction(state,false);
		if(!qValues.containsKey(proceed) & !qValues.containsKey(yield)){
			rewardAndDiscount[0] = 0.0;
			rewardAndDiscount[1] = discountFactor;
			qValues.put(proceed, rewardAndDiscount);
			qValues.put(yield, rewardAndDiscount);
			currentStateAction = proceed;
			return true;
		}
		else if(qValues.get(proceed)[0] < qValues.get(yield)[0]){
			currentStateAction = proceed;
			return true;
		}
		else{
			currentStateAction = yield;
			return false;
		}
	}
	
	//Update the stored Q-value and associated discount factor.
	private void updateQValue(StateAction stateAction, double reward){
		double oldReward = qValues.get(stateAction)[0];
		double newReward = oldReward + (discountFactor*(reward - oldReward));
		if(discountFactor != 0.0){
			discountFactor -= 0.1;
		}
		rewardAndDiscount[0] = newReward;
		rewardAndDiscount[1] = discountFactor;
		qValues.remove(stateAction);
		qValues.put(stateAction, rewardAndDiscount);
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
