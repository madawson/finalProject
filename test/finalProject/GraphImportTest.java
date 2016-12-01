package finalProject;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class GraphImportTest {
	
	DirectedSparseMultigraph<MyNode,MyEdge> g;
	Collection<MyNode> c;
	Collection<MyEdge> ce;
	List<MyNode> l;
	List<MyEdge> le;
	MyNode n;
	MyEdge e;
	String id;
	int check;
	double dcheck;
	String type;
	List<MyNode> nodeList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getNodesTest() {
		g = GraphLoader.importGraph();
		c = g.getVertices();
		l = new ArrayList<MyNode>(c);
		n = l.get(0);
		id = n.getId();
		System.out.println(id);
		id = n.getType();
		System.out.println(id);		
		
	}

	@Test
	public void getEdgesTest() {
		g = GraphLoader.importGraph();
		ce = g.getEdges();
		le = new ArrayList<MyEdge>(ce);
		e = le.get(0);
		dcheck = e.getWeight();
		id = e.getId();
		System.out.println(dcheck);
		System.out.println(id);
		dcheck = e.getThreshold();
		System.out.println(dcheck);
		id = e.getType();
		System.out.println(id);
		check = e.getNumUsers();
		System.out.println(check);	
		check = e.getCapacity();
		System.out.println(check);
		
		
	}
	
	@Test
	public void getNumNodesTest() {
		g = GraphLoader.importGraph();
		check = g.getVertexCount();
		System.out.println(check);
		
	}
	
	@Test
	public void getNumEdgesTest() {
		g = GraphLoader.importGraph();
		check = g.getEdgeCount();
		System.out.println(check);
		
	}
	
	@Test
	public void getNodeTest() {
		
		g = GraphLoader.importGraph();
		
		Collection<MyNode> nodes = g.getVertices();
		nodeList = new ArrayList<MyNode>(nodes);

		Random rnd = new Random();
		float number = rnd.nextFloat();

		if(number<0.3)
			type = "S";		
		else
			type = "L";
		
		MyNode container;
		String checkType;
		List<MyNode> subSetList = new ArrayList<MyNode>();
		for(int i= 0; i < nodeList.size(); i++){
			container = nodeList.get(i);
			checkType = container.getType();
			if(checkType.equals(type))
				subSetList.add(container);
		}
		
		int index = rnd.nextInt(subSetList.size());
		MyNode node = subSetList.get(index);
		
		System.out.print(node.getId());
		
	}
	
	@Test
	public void EdgesTest() {
		g = GraphLoader.importGraph();
		check = g.getEdgeCount();
		System.out.println(check);
		ce = g.getEdges();
		List<MyEdge> edgeList = new ArrayList<MyEdge>(ce);
		for(int i = 0; i < check; i++)
			System.out.println(edgeList.get(i).getId());
		
	}
	
	@Test
	public void NodeTest() {
		g = GraphLoader.importGraph();
		check = g.getVertexCount();
		System.out.println(check);
		c = g.getVertices();
		List<MyNode> nodeList = new ArrayList<MyNode>(c);
		for(int i = 0; i < check; i++)
			System.out.println(nodeList.get(i).getId());
		
	}
}
