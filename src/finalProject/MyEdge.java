package finalProject;

public class MyEdge {
	
	private double weight;
	private int threshold;
	private String id;
	private String type;
	private int numUsers;
	private int capacity;
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public double getWeight(){
		return weight;
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
	
	public void joinEdge(){
		numUsers++;
		if(numUsers >= capacity){
			weight += 0.1;
		}
	}
	
	public void leaveEdge(){
		numUsers--;
		if(numUsers >= capacity){
			weight -= 0.1;
		}
	}
	
}

