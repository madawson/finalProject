package finalProject;

public class StateAction {

	private State state;	
	private boolean proceed;
	
	public StateAction(State state, boolean proceed){
		this.state = state;
		this.proceed = proceed;
	}
	
	public State getState(){
		return state;
	}
	
	public void setState(State state){
		this.state = state;
	}
	
	public boolean getProceed(){
		return proceed;
	}
	
	public void setProceed(boolean proceed){
		this.proceed = proceed;
	}
		
}
