package finalProject;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class MyEdgeTests {
	
	DirectedSparseMultigraph<MyNode,MyEdge> g;
	List<MyEdge> edgesList;
	MyEdge e;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		g = GraphLoader.importGraph();
		Collection<MyEdge> edges = g.getEdges();
		edgesList = new ArrayList<MyEdge>(edges);
		e = edgesList.get(0);
		e.setNumUsers(50);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void checkWeightRecalculate() {
		e.recalculateWeight();
		assertTrue(99.196 < e.getWeight() & e.getWeight() < 99.197);
	}
	
	@Test
	public void checkProgressRate() {
		e.recalculateWeight();
		assertTrue(6.021 < e.getProgressRate() & e.getProgressRate() < 6.022);
	}
	
	@Test
	public void checkJoinAndLeave() {
		e.joinEdge();
		assertTrue(e.getNumUsers() == 51);
		e.leaveEdge();
		assertTrue(e.getNumUsers() == 50);
	}

}
