package finalProject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class NodeSelectorTests {
	
	DirectedSparseMultigraph<MyNode,MyEdge> g;
	NodeSelector nodeSelector;
	String sType = "S";
	String lType = "L";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		g = GraphLoader.importGraph();	
		nodeSelector = new NodeSelector(g);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void checkNodeList(){
		assertTrue(nodeSelector.nodeList.size() == 39);
	}
	
	@Test
	public void checkSList() {
		for(int i = 0; i < nodeSelector.sList.size(); i++){
			assertTrue(nodeSelector.sList.get(i).getType() != "S");
		}
	}
	
	@Test
	public void checkLList(){
		for(int i = 0; i < nodeSelector.lList.size(); i++){
			assertTrue(nodeSelector.lList.get(i).getType() != "L");
		}		
	}
	
	@Test
	public void checkSelection(){
		for(int i = 0; i < 10; i++){
			System.out.println(nodeSelector.getNode().getType());
		}
	}

}
