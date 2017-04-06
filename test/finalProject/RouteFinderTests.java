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

public class RouteFinderTests {
	
	DirectedSparseMultigraph<MyNode,MyEdge> g;
	RouteFinder routeFinder;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		g = GraphLoader.importGraph();	
		routeFinder = new RouteFinder(g);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void checkOptimal() {
		String test1 = "Egham";
		String test2 = "Wokingham";
		List<MyNode> nodeList;
		MyNode n;
		Collection<MyNode> nodes = g.getVertices();
		nodeList = new ArrayList<MyNode>(nodes);
		MyNode startNode = nodeList.get(0);
		MyNode endNode = nodeList.get(0);
		
		for(int i = 0; i < nodeList.size(); i++){
			n = nodeList.get(i);
			if(n.getId().equals(test1)){
				startNode = n;
			}
			else if(n.getId().equals(test2)){
				endNode = n;
			}
		}
		
		assertTrue(routeFinder.getDistance(startNode, endNode) == 340.0);

	}

}
