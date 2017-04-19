package finalProject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class LearningAgentTests {
	
	DirectedSparseMultigraph<MyNode, MyEdge> g;
	LearningAgent testAgent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//Import the test graph (contains three nodes and two edges between each).
		g = GraphLoader.importGraph();
		
		//Create the test agent.
		NodeSelector n = new NodeSelector(g);
		RouteFinder r = new RouteFinder(g);
		Supervisor s = new Supervisor(5000);
		testAgent = new LearningAgent(n,r,s);
		
		//Obtain a start node and an end node.
		testAgent.startNode = n.getNode();
		testAgent.endNode = n.getNode();
		while(testAgent.startNode == testAgent.endNode){
			testAgent.endNode = n.getNode();
		}
		
		System.out.println("Start node = " + testAgent.startNode.getId());
		System.out.println("End node = " + testAgent.endNode.getId());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testShortestPath() {
		testAgent.path = testAgent.routeFinder.getShortestRoute(testAgent.startNode, testAgent.endNode);
		assertTrue(testAgent.path.size() == 1);
	}
	
	@Test 
	public void testJourneyWeightEstimation(){
		testAgent.path = testAgent.routeFinder.getShortestRoute(testAgent.startNode, testAgent.endNode);
		double estimatedWeight = testAgent.calculateEstimatedJourneyWeight(testAgent.path);
		System.out.println("Shortest path length = " + estimatedWeight);
		System.out.println("Shortest path edges = " + testAgent.path.get(0).getId());
		
	}
	
	@Test 
	public void testSecondShortestPath(){
		testAgent.path = testAgent.routeFinder.getShortestRoute(testAgent.startNode, testAgent.endNode);
		testAgent.secondPath(testAgent.routeFinder);
		assertTrue(testAgent.secondPath.size() == 2);
		double estimatedWeight = testAgent.calculateEstimatedJourneyWeight(testAgent.secondPath);
		System.out.println("Second shortest path length = " + estimatedWeight);
		System.out.println("Second shortest path edges = " + testAgent.secondPath.get(0).getId() + " " + testAgent.secondPath.get(1).getId());
		
	}

}
