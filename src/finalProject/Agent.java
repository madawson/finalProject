package finalProject;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class Agent {
	
	NodeSelector nodeSelector;
	RouteFinder routeFinder;
	MyNode startNode;
	MyNode endNode;
	List<MyEdge> path;
	
	public Agent(){
		
	}

	public Agent(NodeSelector nodeSelector, RouteFinder routeFinder){
		nodeSelector = this.nodeSelector;
		routeFinder = this.routeFinder;
		
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
		path = routeFinder.getRoute(startNode, endNode);
	}
	
	@ScheduledMethod(start = 1, interval = 1) 
	public void step(){
		
		
		
		//TODO logic executed at every iteration.
		System.out.println("I Exist!");
		
		
		
	}
	

	
	
	
	/*	@Watch(watcheeClassName = "aClassName",
			watcheeFieldNames = "aFieldName",
			query = "aQuery",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void watch(){
		//TODO logic executed only when something happens.

	}*/

}
