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
	
	int check;
	MyEdge e;
	
	public Agent(){
		
	}

	public Agent(NodeSelector nodeSelector, RouteFinder routeFinder){
		this.nodeSelector = nodeSelector;
		this.routeFinder = routeFinder;
		
		startNode = nodeSelector.getNode();
		endNode = nodeSelector.getNode();
		
		path = routeFinder.getRoute(startNode, endNode);
	}
	
	@ScheduledMethod(start = 1, interval = 1) 
	public void step(){
		
		
		
		//TODO logic executed at every iteration.
		System.out.println("I Exist!");
		System.out.println("My start node is: " + startNode.getId());
		System.out.println("My end node is: " + endNode.getId());
		System.out.println("My path size is: " + path.size());
		
	/*	for(int i = 0; i < path.size(); i++){
			e = path.get(i);
			System.out.println(e.getId()); 
		} */
		
		
	}
	

	
	
	
	/*	@Watch(watcheeClassName = "aClassName",
			watcheeFieldNames = "aFieldName",
			query = "aQuery",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void watch(){
		//TODO logic executed only when something happens.

	}*/

}
