package finalProject;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class MyNode{
		
	/**
	 * The node type; either 'S' or 'L'.
	 */
	private String type;
	
	/**
	 * Unique node ID.
	 */
	private String id;
	
	/**
	 * Set the node type.
	 * @param type is either 'S' or 'L'.
	 */
	public void setType(String type){
		this.type = type;
	}
	
	/**
	 * Get the type of this node.
	 * @return type of this node.
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Set this node ID.
	 * @param id is unique for each node.
	 */
	public void setId(String id){
		this.id = id;
	}
	
	/**
	 * Get the ID of this node.
	 * @return ID of this node.
	 */
	public String getId(){
		return id;
	}
}
