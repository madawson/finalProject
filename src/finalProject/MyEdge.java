package finalProject;

public class MyEdge {
	
	private double initialWeight;
	private double weight;
	private double excessWeight;
	private double initialProgressRate;
	private int threshold;
	private String id;
	private String type;
	private int numUsers;
	private double excess;
	private int capacity;
	private boolean capacityBreached;
	
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
	
	public void joinEdge(){
		numUsers++;
		if(!capacityBreached & (numUsers == capacity))
			capacityBreached = true;
		if(numUsers >= capacity){
			recalculateWeight();
		}
	}
	
	public void leaveEdge(){
		numUsers--;
		if(capacityBreached & (numUsers == capacity))
			capacityBreached = false;
		if(numUsers >= capacity){
			recalculateWeight();
		}
	}
	
	public double getProgressRate(){
		if(numUsers <= capacity)
			return initialProgressRate;
		else
			//Progress is a function of excess edge weight.
			return initialProgressRate - (Math.tanh(excessWeight/20)*4);
	}
	
	public void recalculateWeight(){
		excess = numUsers - capacity;
		excessWeight = (Math.tanh(excess/10)*60);
		weight = initialWeight + excessWeight;
	}
	
//------------------Data Gathering Methods---------------------------------------------------------------------------------------
	
	public boolean getCapacityBreached(){
		return capacityBreached;
	}
	
}

