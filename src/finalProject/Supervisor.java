package finalProject;

import java.util.HashMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

public class Supervisor {
	
	private double probability;
	private double numAgents;
	private double targetNumAgents;
	private double upperBound;
	private double lowerBound;
	private double upperDistance;
	private HashMap<Double, Double> targetNumbers;
	private double tickCount;
	private double scaleFactor;
	private int totalJourneyLength;
	private int stepTotalJourneyLength;
	private int averageTotalJourneyLength;
	private int tracker;
	
	public Supervisor(int agentCount){
		
		probability = 0.0;

		//Construct the target table.
		targetNumbers = new HashMap<Double, Double>();
		targetNumbers.put(0.0, 10.0);
		targetNumbers.put(1.0, 3.0);
		targetNumbers.put(2.0, 2.0);
		targetNumbers.put(3.0, 3.0);
		targetNumbers.put(4.0, 10.0);
		targetNumbers.put(5.0, 25.0);
		targetNumbers.put(6.0, 100.0);
		targetNumbers.put(7.0, 175.0);
		targetNumbers.put(8.0, 175.0);
		targetNumbers.put(9.0, 135.0);
		targetNumbers.put(10.0, 100.0);
		targetNumbers.put(11.0, 100.0);
		targetNumbers.put(12.0, 100.0);
		targetNumbers.put(13.0, 100.0);
		targetNumbers.put(14.0, 130.0);
		targetNumbers.put(15.0, 150.0);
		targetNumbers.put(16.0, 200.0);
		targetNumbers.put(17.0, 210.0);
		targetNumbers.put(18.0, 150.0);
		targetNumbers.put(19.0, 100.0);
		targetNumbers.put(20.0, 50.0);
		targetNumbers.put(21.0, 35.0);
		targetNumbers.put(22.0, 20.0);
		targetNumbers.put(23.0, 10.0);
				
		scaleFactor = agentCount/250;
		
		numAgents= 0.0;
		
		stepTotalJourneyLength = 0;
		totalJourneyLength = 0;
		averageTotalJourneyLength = 0;
		
		tracker = 0;
	}
	
	@ScheduledMethod(start = 2, interval = 1) 
	public void postStep(){
		
		tracker++;
		tickCount = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		targetNumAgents = (getTarget(tickCount))*scaleFactor;
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
		
	public void incrementNumAgents(){
		numAgents += 1.0;
	}
	
	public void decrementNumAgents(){
		numAgents -= 1.0;
	}
	
	private double calculateStepChange(double distance){
		double stepChange;
		stepChange = (Math.log(distance/(1-distance))+4.6)/1060;
		return stepChange;
	}
	
	private double getTarget(double tick){	
		double i = tick % 1440;
		i = i/60;
		return targetNumbers.get(Math.floor(i));
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
