package finalProject;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class MyEdge {
	
	// The edge class represents a road in the network. Agents' rate of progress is determined within this class.
	
	/**
	 * Unique ID for each edge.
	 */
	private String id;
	
	/**
	 * Either "M" or "A".
	 */
	private String type;	
	
	/**
	 * Predetermined value based on the type of the edge.
	 */
	private double initialWeight;			
	
	/**
	 * The live edge weight.
	 */
	private double weight;		
	
	/**
	 * Total difference between initialWeight and weight.
	 */
	private double excessWeight;
	
	/**
	 * Initial rate at which agents may progress along an edge (i.e. when the number of edge users is below capacity.)
	 */
	private double initialProgressRate;	
	
			
	
	/**
	 * Predetermined value. Edge weight begins to increase when numUsers breaches capacity.
	 */
	private int capacity;		
	
	/**
	 * Predetermined value. Agents begin receiving a congestion warning then numUsers breaches threshold.
	 */
	private int threshold;	
	
	/**
	 * The number of active agents currently using the edge.
	 */
	private int numUsers;					
	
	/**
	 * Returns true when numUsers breaches capacity.
	 */
	private boolean capacityBreached;		
	
	/**
	 * Set the weight of this edge.
	 * @param weight is used to calculate the rate at which agents progress along an edge.
	 */
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	/**
	 * Get the weight of this edge.
	 * @return weight of this edge.
	 */
	public double getWeight(){
		return weight;
	}
	
	/**
	 * Set value of the weight of this edge for the first time.
	 * @param weight is used to calculate the rate at which agents progress along an edge.
	 */
	public void setInitialWeight(double weight){
		this.initialWeight = weight;
	}
	
	/**
	 * Get the first value that was set for the weight of this edge.
	 * @return this first value that was set for the weight of this edge.
	 */
	public double getInitialWeight(){
		return initialWeight;
	}
	
	/**
	 * Set the threshold of this edge.
	 * @param threshold is used by agents to predict future congestion.
	 */
	public void setThreshold(int threshold){
		this.threshold = threshold;
	}
	
	/**
	 * Get the threshold of this edge.
	 * @return threshold of this edge.
	 */
	public int getThreshold(){
		return threshold;
	}
	
	/**
	 * Set the capacity of this edge.
	 * @param capacity is used to decide whether the edge weight should be altered.
	 */
	public void setCapacity(int capacity){
		this.capacity = capacity;
	}
	
	/**
	 * Get the capacity of this edge.
	 * @return capacity of this edge.
	 */
	public int getCapacity(){
		return capacity;
	}	
	
	/**
	 * Set the unique ID for this edge.
	 * @param id is unique for each edge.
	 */
	public void setId(String id){
		this.id = id;
	}
	
	/**
	 * Get the unique ID for this edge.
	 * @return unique ID for this edge.
	 */
	public String getId(){
		return id;
	}
	
	
	/**
	 * Set the type of this edge.
	 * @param type corresponds to either "M" or "A".
	 */
	public void setType(String type){
		this.type = type;
	}
	
	/**
	 * Get the type of this edge.
	 * @return the type of this edge.
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Set the number of users on this edge.
	 * @param numUsers is the number of active agents currently using this edge.
	 */
	public void setNumUsers(int numUsers){
		this.numUsers = numUsers;
	}
	
	/**
	 * Get the number of users on this edge.
	 * @return the number of active agents currently using this edge.
	 */
	public int getNumUsers(){
		return numUsers;
	}
	
	/**
	 * Set the initial rate at which agents may progress along this edge.
	 * @param rate is related to the number of active agent using the edge. Higher numUsers = slower rate and vice versa.
	 */
	public void setInitialProgressRate(double rate){
		initialProgressRate = rate;
	}
	
	/**
	 * Get the initial rate at which agents may progress along this edge.
	 * @return rate at which agents may progress along this edge.
	 */
	public double getInitialProgressRate(){
		return initialProgressRate;
	}
	
	/**
	 * Used by an agent to join this edge. Edge Weight is recalculated immediately post-joining.
	 */
	public void joinEdge(){
		numUsers++;
		if(!capacityBreached & (numUsers == capacity))
			capacityBreached = true;
		if(numUsers >= capacity){
			recalculateWeight();
		}
	}
	
	/**
	 * Used by an agent to leave this edge. Edge weight is recalculated immediately post-leaving.
	 */
	public void leaveEdge(){
		numUsers--;
		if(capacityBreached & (numUsers == capacity))
			capacityBreached = false;
		if(numUsers >= capacity){
			recalculateWeight();
		}
	}
	
	/**
	 * Used by an agent to retrieve the rate of progress at each time step.
	 * @return rate at which an agent may progress along this edge.
	 */
	public double getProgressRate(){
		if(numUsers <= capacity)
			return initialProgressRate;
		else
			//Progress is a function of excess edge weight.
			return initialProgressRate - (Math.tanh(excessWeight/20)*4);
	}
	
	/**
	 * Recalculates the edge weight based on how many users above capacity are currently using the edge.
	 */	
	public void recalculateWeight(){
		double excessUsers = numUsers - capacity;
		excessWeight = (Math.tanh(excessUsers/10)*60);
		weight = initialWeight + excessWeight;
	}
	
//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	/**
	 * Used for data gathering.
	 * @return whether or not the number of active users on this edge has breached the capacity of this edge.
	 */
	public boolean getCapacityBreached(){
		return capacityBreached;
	}
	
	public boolean getMRoadBreached(){
		if(this.type.equals("M") & capacityBreached){
			return true;
		}
		return false;
	}
	
	public boolean getARoadBreached(){
		if(this.type.equals("A") & capacityBreached){
			return true;
		}
		return false;		
	}
	
}

