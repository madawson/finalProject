package finalProject;

public class MyNode{
	
	/*Each node consists of a type (either 'S' or 'L') and a unique ID.
	 */
	
	private String type;
	private String id;
		
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
}
