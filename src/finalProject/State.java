package finalProject;

public class State {
	
//------State Variables-------------------------
	private double timeIndex;
	private boolean warningReceived;
	
		
	public State(double timeIndex, boolean warningReceived){
		this.timeIndex = timeIndex;
		this.warningReceived = warningReceived;
	}

	public double getTimeIndex(){
		return timeIndex;
	}
	
	public void setTimeIndex(double timeIndex){
		this.timeIndex = timeIndex;
	}
	
	public boolean getWarningReceived(){
		return warningReceived;
	}
	
	public void setWarningReceived(boolean warningReceived){
		this.warningReceived = warningReceived;
	}
}
