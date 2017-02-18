package finalProject;

public class MyEdge {
	
	// The edge class represents a road in the network. Agents' rate of progress is determined within this class.
	
	private String id;						//Unique for each edge.
	private String type;					//Either 'M' or 'A'
	private double initialWeight;			//Predetermined value based on the type of the edge.
	private double weight;					//The live edge weight.
	private double excessWeight;			//Total weight above the initialWeight.
	private double initialProgressRate;		
	private double excessUsers;				//The number of users above the capacity.
	private int capacity;					//Predetermined value. Edge weight begins to increase when numUsers breaches capacity.
	private int threshold;					//Predetermined value. Agents begin receiving a congestion warning then numUsers breaches threshold.
	private int numUsers;					//The number of active agents currently using the edge.
	private boolean capacityBreached;		//Returns true when numUsers breaches capacity.
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setInitialWeight(double weight){
		this.initialWeight = weight;
	}
	
	public double getInitialWeight(){
		return initialWeight;
	}
	
	public void setThreshold(int threshold){
		this.threshold = threshold;
	}
	
	public int getThreshold(){
		return threshold;
	}
	
	public void setCapacity(int capacity){
		this.capacity = capacity;
	}
	
	public int getCapacity(){
		return capacity;
	}	
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
	public void setNumUsers(int numUsers){
		this.numUsers = numUsers;
	}
	
	public int getNumUsers(){
		return numUsers;
	}
	
	public void setInitialProgressRate(double rate){
		initialProgressRate = rate;
	}
	
	public double getInitialProgressRate(){
		return initialProgressRate;
	}
	
	//Used by an agent to join this edge;
	public void joinEdge(){
		numUsers++;
		if(!capacityBreached & (numUsers == capacity))
			capacityBreached = true;
		if(numUsers >= capacity){
			recalculateWeight();
		}
	}
	
	//Used by an agent to leave this edge.
	public void leaveEdge(){
		numUsers--;
		if(capacityBreached & (numUsers == capacity))
			capacityBreached = false;
		if(numUsers >= capacity){
			recalculateWeight();
		}
	}
	
	//Used by an agent to retrieve the rate of progress at each time step.
	public double getProgressRate(){
		if(numUsers <= capacity)
			return initialProgressRate;
		else
			//Progress is a function of excess edge weight.
			return initialProgressRate - (Math.tanh(excessWeight/20)*4);
	}
	
	public void recalculateWeight(){
		excessUsers = numUsers - capacity;
		excessWeight = (Math.tanh(excessUsers/10)*60);
		weight = initialWeight + excessWeight;
	}
	
//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	public boolean getCapacityBreached(){
		return capacityBreached;
	}
	
}

