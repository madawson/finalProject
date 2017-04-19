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

public class GraphImportTest {
	
	DirectedSparseMultigraph<MyNode,MyEdge> g;
	Collection<MyNode> nodes;
	Collection<MyEdge> edges;
	List<MyNode> nodesArrayList;
	List<MyEdge> edgesArrayList;
	MyNode n;
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
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void checkNumberOfNodes(){
		
		nodes = g.getVertices();
		nodesArrayList = new ArrayList<MyNode>(nodes);
				
		assertTrue(nodesArrayList.size() == 3);
	}
	
	@Test
	public void checkNumberOfEdges(){
		
		edges = g.getEdges();
		edgesArrayList = new ArrayList<MyEdge>(edges);
		
		assertTrue(edgesArrayList.size() == 6);		
	}
	
	@Test
	public void checkMRoads(){

		edges = g.getEdges();
		edgesArrayList = new ArrayList<MyEdge>(edges);
		for(int i = 0; i < edgesArrayList.size(); i++){
			e = edgesArrayList.get(i);
			if(e.getType() == "M"){
				e = edgesArrayList.get(i);
				assertTrue(e.getInitialWeight() == 40.0);
				assertTrue(e.getWeight() == 40.0);
				assertTrue(e.getThreshold() == 20);
				assertTrue(e.getCapacity() == 25);
				assertTrue(e.getNumUsers() == 0);
				assertTrue(e.getInitialProgressRate() == 10.0);
			}
		}		
	}
	
	@Test
	public void checkARoads(){

		edges = g.getEdges();
		edgesArrayList = new ArrayList<MyEdge>(edges);
		for(int i = 0; i < edgesArrayList.size(); i++){
			e = edgesArrayList.get(i);
			if(e.getType() == "A"){
				e = edgesArrayList.get(i);
				assertTrue(e.getInitialWeight() == 60.0);
				assertTrue(e.getWeight() == 60.0);
				assertTrue(e.getThreshold() == 15);
				assertTrue(e.getCapacity() == 20);
				assertTrue(e.getNumUsers() == 0);
				assertTrue(e.getInitialProgressRate() == 5.0);
			}
		}	
	}
	
	@Test
	public void checkNumberOfMRoads(){
		
		int count = 0;
		edges = g.getEdges();
		edgesArrayList = new ArrayList<MyEdge>(edges);
		for(int i = 0; i < edgesArrayList.size(); i++){
			e = edgesArrayList.get(i);
			if(e.getType().equals("M")){
				count++;
			}
		}
		
		System.out.println(count);
		assertTrue(count == 2);
		
	}
	
	@Test
	public void checkNumberOfARoads(){
		
		int count = 0;
		edges = g.getEdges();
		edgesArrayList = new ArrayList<MyEdge>(edges);
		for(int i = 0; i < edgesArrayList.size(); i++){
			e = edgesArrayList.get(i);
			if(e.getType().equals("A")){
				count++;
			}
		}
		
		System.out.println(count);
		assertTrue(count == 4);
		
	}

}
