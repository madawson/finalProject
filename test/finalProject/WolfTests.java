package finalProject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class WolfTests {
	
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
		testAgent.qLearning = true;
		testAgent.wolfPhc = true;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void checkPolicyInitialisation() {
		assertTrue(testAgent.mixedPolicy.length == 48);
		for(int i = 0; i<48; i++){			
				assertTrue(testAgent.mixedPolicy[i].length == 2);		
		}
		
		for(int i = 0; i<48; i++){
			for(int j = 0; j<2; j++){
				assertTrue(testAgent.mixedPolicy[i][j] == 0.5);
			}
		}
	}
	
	@Test
	public void checkConstrain() {
		testAgent.updateQValue(testAgent.stateActionArray[32][0], -1000.0);
		testAgent.updateMixedPolicy(32, 0);
		
		assertTrue(testAgent.mixedPolicy[32][0] > 0.0);
		assertTrue(testAgent.mixedPolicy[32][0] < 1.0);
		assertTrue((testAgent.mixedPolicy[32][0] + testAgent.mixedPolicy[32][0]) > 0.99);
		
	}
	
	@Test
	public void checkPolicyUpdate() {
		testAgent.updateQValue(testAgent.stateActionArray[32][0], -1000.0);
		testAgent.updateMixedPolicy(32, 0);
		
		assertTrue(0.545 < testAgent.mixedPolicy[32][0] & testAgent.mixedPolicy[32][0] < 0.546);
		assertTrue(0.454 < testAgent.mixedPolicy[32][1] & testAgent.mixedPolicy[32][1] < 0.456);
				
	}
	
	@Test
	public void checkAverageUpdate() {
				
		
		testAgent.updateAveragePolicy(32);
								
		testAgent.updateQValue(testAgent.stateActionArray[32][0], 1000.0);

		testAgent.updateMixedPolicy(32, 0);
				
		testAgent.updateAveragePolicy(32);

				
		System.out.println("Printing mixed policy...");
		for(int i = 0; i<48; i++){
			for(int j = 0; j<2; j++){
				System.out.println(testAgent.mixedPolicy[i][j]);
			}
		}
		
		System.out.println("Printing Average policy...");
		for(int i = 0; i<48; i++){
			for(int j = 0; j<2; j++){
				System.out.println(testAgent.averagePolicy[i][j]);
			}
		}
		
	}
	
	@Test
	public void checkWinningLosing() {
		testAgent.updateQValue(testAgent.stateActionArray[32][0], -1000.0);
		assertTrue(testAgent.winning(32, 0, 1) == true);
		
	}

}
