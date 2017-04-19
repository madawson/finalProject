package finalProject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class QLearningTests {
	
	LearningAgent testAgent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		DirectedSparseMultigraph<MyNode,MyEdge> g = GraphLoader.importGraph();
		NodeSelector n = new NodeSelector(g);
		RouteFinder r = new RouteFinder(g);
		Supervisor s = new Supervisor(5000);
		testAgent = new LearningAgent(n,r,s);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void hashTableNotEmpty() {
		assertFalse(testAgent.qValues.isEmpty() == true);
	}
		
	@Test
	public void checkQValueUpdate(){
		testAgent.updateQValue(0, 11.0);
		double discountFactor = testAgent.discountFactor;
		assertTrue(testAgent.qValues.get(0)[1] == discountFactor);
		assertTrue(testAgent.qValues.get(0)[0] == (0.0 + (discountFactor*11.0)));
	}
	
	@Test
	public void hashTableCorrectSize(){
		assertTrue(testAgent.qValues.size() == 96);
	}
	
	@Test
	public void checkSoftmax(){
		testAgent.updateQValue(0, -8.0);
		testAgent.updateQValue(1, -10.0);
		double probability = testAgent.getSoftMaxProbability(0, 1);
		System.out.println(probability);
	}
}
