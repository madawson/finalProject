package finalProject;

public class MyEdge {
	
	private double weight;
	private double threshold;
	private String id;
	private String type;
	private int numUsers;
	private int capacity;
	
	public void setWeight(double weight){
		weight = this.weight;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setThreshold(double threshold){
		threshold = this.threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public void setId(String id){
		id = this.id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setType(String type){
		type=this.type;
	}
	
	public String getType(){
		return type;
	}
	
	public void joinEdge(){
		numUsers++;
		if(numUsers >= capacity){
			weight += 0.1;
		}
	}
	
	public void leaveEdge(){
		numUsers--;
	}
}

