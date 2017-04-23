package finalProject;

import java.util.HashMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class Supervisor {
	
	private HashMap<Double, Double> targetNumbers;		//The active target numbers hash table.
	private HashMap<Double, Double> targetNumbers1;		//Stores the target NoAA value for each time interval for scenario 1.
	private HashMap<Double, Double> targetNumbers2;		//Stored the target NoAA value for each time interval for scenario 2.
	/**
	 * The probability used by agents to decide whether to begin a journey or not. 
	 */
	private double probability;
	
	/**
	 * The current number of active agents. Agents use the associated utility methods to increment 
	 * or decrement this value when they become active or stop being active, respectively. 
	 */
	private double numAgents;
	
	/**
	 * The desired number of active agents for the current time step, taken from the target table.
	 */
	private double targetNumAgents;
	
	/**
	 * When the number of active agents reaches this value, the probability that agents will start a new journey is 
	 * minimised. Calculated using targetNumAgents. 
	 */
	private double upperBound;
	
	/**
	 * When the number of active agents reaches this value, the probability that agents will start a new journey is
	 * maximised. Calculated using targetNumAgents. 
	 */
	private double lowerBound;
	
	/**
	 * The difference between upperBound and numAgents. 
	 */
	private double upperDistance;
	
	/**
	 * Used to scale the targetNumAgents and associated values based on the total number of agents within the system.
	 */
	private double scaleFactor;
	
	private int totalJourneyLength;
	private int stepTotalJourneyLength;
	private int averageTotalJourneyLength;
	private int tracker;
	
	public Supervisor(int totalAgentCount){
		
		probability = 0.0;

		//Construct the target table for scenario 1 (mid-week).
		targetNumbers1 = new HashMap<Double, Double>();
		targetNumbers1.put(0.0, 10.0);
		targetNumbers1.put(1.0, 3.0);
		targetNumbers1.put(2.0, 2.0);
		targetNumbers1.put(3.0, 3.0);
		targetNumbers1.put(4.0, 10.0);
		targetNumbers1.put(5.0, 25.0);
		targetNumbers1.put(6.0, 100.0);
		targetNumbers1.put(7.0, 175.0);
		targetNumbers1.put(8.0, 175.0);
		targetNumbers1.put(9.0, 135.0);
		targetNumbers1.put(10.0, 100.0);
		targetNumbers1.put(11.0, 100.0);
		targetNumbers1.put(12.0, 100.0);
		targetNumbers1.put(13.0, 100.0);
		targetNumbers1.put(14.0, 130.0);
		targetNumbers1.put(15.0, 150.0);
		targetNumbers1.put(16.0, 200.0);
		targetNumbers1.put(17.0, 210.0);
		targetNumbers1.put(18.0, 150.0);
		targetNumbers1.put(19.0, 100.0);
		targetNumbers1.put(20.0, 50.0);
		targetNumbers1.put(21.0, 35.0);
		targetNumbers1.put(22.0, 20.0);
		targetNumbers1.put(23.0, 10.0);
		
		targetNumbers2 = new HashMap<Double, Double>();
		targetNumbers2.put(0.0, 10.0);
		targetNumbers2.put(1.0, 3.0);
		targetNumbers2.put(2.0, 2.0);
		targetNumbers2.put(3.0, 3.0);
		targetNumbers2.put(4.0, 10.0);
		targetNumbers2.put(5.0, 25.0);
		targetNumbers2.put(6.0, 65.0);
		targetNumbers2.put(7.0, 105.0);
		targetNumbers2.put(8.0, 145.0);
		targetNumbers2.put(9.0, 200.0);
		targetNumbers2.put(10.0, 190.0);
		targetNumbers2.put(11.0, 180.0);
		targetNumbers2.put(12.0, 170.0);
		targetNumbers2.put(13.0, 160.0);
		targetNumbers2.put(14.0, 150.0);
		targetNumbers2.put(15.0, 150.0);
		targetNumbers2.put(16.0, 130.0);
		targetNumbers2.put(17.0, 100.0);
		targetNumbers2.put(18.0, 80.0);
		targetNumbers2.put(19.0, 60.0);
		targetNumbers2.put(20.0, 50.0);
		targetNumbers2.put(21.0, 35.0);
		targetNumbers2.put(22.0, 20.0);
		targetNumbers2.put(23.0, 10.0);
				
		targetNumbers = targetNumbers1;
		
		scaleFactor = totalAgentCount/250;
		
		numAgents= 0.0;
		
		stepTotalJourneyLength = 0;
		totalJourneyLength = 0;
		averageTotalJourneyLength = 0;
		
		tracker = 0;
	}
	
	//------------------Post-Step Method---------------------------------------------------------------------------------------
	
	@ScheduledMethod(start = 2, interval = 1) 
	public void postStep(){
				
		tracker++;
		targetNumAgents = (getTarget())*scaleFactor;
		upperBound = targetNumAgents + (10*scaleFactor);
		lowerBound = targetNumAgents - (10*scaleFactor);
		if(numAgents >= upperBound)
			probability = 0.0;
		else if(numAgents <= lowerBound)
			probability = 0.01;
		else{
			upperDistance = (upperBound - numAgents)/800;
			probability = calculateStepChange(upperDistance);
		}
		
		stepTotalJourneyLength = stepTotalJourneyLength + totalJourneyLength;
		
		if(tracker == 19){
			averageTotalJourneyLength = stepTotalJourneyLength/20;
			totalJourneyLength = 0;
			stepTotalJourneyLength = 0;
			tracker = 0;
		}
		
		//Saturday scenario
		/*
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		int index = (int) Math.floor((tick % 10080)/1440);
		if(index == 5 || index == 6){
			targetNumbers = targetNumbers2;
		}
		else
			targetNumbers = targetNumbers1;
		*/
		
		
	}
	
	//------------------Utility Methods---------------------------------------------------------------------------------------
	
	/**
	 * Increase the number of active agents by 1.
	 */
	public void incrementNumAgents(){
		numAgents += 1.0;
	}
	
	/**
	 * Decrease the number of active agents by 1.
	 */
	public void decrementNumAgents(){
		numAgents -= 1.0;
	}
	
	/**
	 * Used to calculate the required changed in probability depending on the distance of numAgents from upperBound.
	 * @param distance is the difference between numAgents and upperBound.
	 * @return The amount by which the probability should be adjusted.
	 */
	private double calculateStepChange(double distance){
		double stepChange;
		stepChange = (Math.log(distance/(1-distance))+4.6)/1060;
		return stepChange;
	}
	
	/**
	 * Obtains the desired number of active agents from the table.
	 * @return The target number of active agents (pre-scaling).
	 */
	private double getTarget(){	
		return targetNumbers.get(getTimeIndex());
	}
		
	/**
	 * Uses the current system tick to calculate an integer value between 0-23.
	 * @return An integer value between 0-23 corresponding to an index in the table of target values.
	 */
	public double getTimeIndex(){
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return Math.floor((tick % 1440)/60);
	} 
	
	
	//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	/**
	 * Used for data gathering. Return the current probability.
	 * @return The current probability.
	 */
	public double getProbability(){
		return probability;
	}
	
	/**
	 * Used for data gathering. Returns the current average journey length for all agents. 
	 * @return The average journey length for all agents. 
	 */
	public int getStepTotalJourneyLength(){
		return averageTotalJourneyLength;
	}
	
	public void appendJourneyLength(int journeyLength){
		totalJourneyLength = totalJourneyLength + journeyLength;
	}

}
