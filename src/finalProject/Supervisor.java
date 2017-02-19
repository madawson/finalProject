package finalProject;

import java.util.HashMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

public class Supervisor {
	
	private HashMap<Double, Double> targetNumbers;		//The active target numbers hash table.
	private HashMap<Double, Double> targetNumbers1;		//Stores the target NoAA value for each time interval for scenario 1.
	private HashMap<Double, Double> targetNumbers2;		//Stored the target NoAA value for each time interval for scenario 2.
	private double probability;
	private double numAgents;
	private double targetNumAgents;
	private double upperBound;
	private double lowerBound;
	private double upperDistance;
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
	}
	
	//------------------Utility Methods---------------------------------------------------------------------------------------
		
	public void incrementNumAgents(){
		numAgents += 1.0;
	}
	
	public void decrementNumAgents(){
		numAgents -= 1.0;
	}
	
	//Used to calculate the required changed in probability depending on the distance of the NoAA from the upper bound.
	private double calculateStepChange(double distance){
		double stepChange;
		stepChange = (Math.log(distance/(1-distance))+4.6)/1060;
		return stepChange;
	}
	
	private double getTarget(){	
		return targetNumbers.get(getTimeIndex());
	}
	
	public void appendJourneyLength(int journeyLength){
		totalJourneyLength = totalJourneyLength + journeyLength;
	}
	
	public double getTimeIndex(){
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return Math.floor((tick % 1440)/60);
	} 
	
	
	//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	public double getProbability(){
		return probability;
	}
	
	public int getStepTotalJourneyLength(){
		return averageTotalJourneyLength;
	}

}
