package finalProject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class GraphGenerator {
			
		static DirectedSparseMultigraph<Integer, Integer> g;
		static List<Integer> sNodeList = new ArrayList<Integer>();
		static List<Integer> lNodeList = new ArrayList<Integer>();
		
		public static void main(String[] args) throws FileNotFoundException {
			
			buildGraph(20, 2);
			categoriseNodes();
			
			//Graph properties	
			int initialNumNodes = g.getVertexCount();
			int initialNumEdges = g.getEdgeCount();
		
			//Set the output to a text file.
			PrintStream out = new PrintStream(new FileOutputStream("graph.txt"));
			System.setOut(out);
						
			//Print GraphML and parse information
			System.out.println("Number of nodes:" + initialNumNodes);
			System.out.println("\n");
			System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			System.out.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\""
					+ " \n xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
					+ " \n xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns"
					+ " \n http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">");
			System.out.println("\n");
			System.out.println("<!-- Attributes: edge weights, edge types and node types -->");
			System.out.println("<key id=\"d2\" for=\"edge\" attr.name=\"type\" attr.type=\"string\">"
							 + "\n <default>A</default>"
							 + "\n </key>");
			System.out.println("\n");
			System.out.println("<key id=\"d3\" for=\"node\" attr.name=\"type\" attr.type=\"string\">"
							 + "\n <default>T</default>"
							 + "\n </key>");
			System.out.println("\n");
			System.out.println("<!-- Parse information -->");
			System.out.println("<graph id=\"Graph\" edgedefault=\"directed\""
					+ "\n parse.nodes=\"" + initialNumNodes + "\""
					+ "\n parse.edges=\"" + initialNumEdges + "\""
					+ "\n parse.nodeids=\"free\""
					+ "\n parse.edgeids=\"free\""
					+ "\n parse.order=\"nodesfirst\">");
			System.out.println("\n");
			
			System.out.println("  <!-- Location nodes -->");
			
			printNodes();
			
			System.out.println("\n");
			System.out.println("  <!-- A-Roads -->");
			
			printEdges();
			
			System.out.println("\n");

			System.out.println("</graph>");
			System.out.println("</graphml>");
		}
		
		public static void printNodes(){
			//Print S-Nodes	
			int id = 0;
			for(int i = 0; i < sNodeList.size(); i++){
				id = sNodeList.get(i);
				System.out.println(createSNode(id));
			}
		
			//Print L-Nodes
			for(int i = 0; i < lNodeList.size(); i++){
				id = lNodeList.get(i);
				System.out.println(createLNode(id));				
			}
		}
		
		public static void printEdges(){
			Collection<Integer> edges;
			List<Integer> edgesArrayList;
			Integer currentEdge;
			int source = 0;
			int dest = 0;
			
			//Print A-Roads
			edges = g.getEdges();
			edgesArrayList = new ArrayList<Integer>(edges);
			
			for(int i = 0; i < edgesArrayList.size(); i++){
				currentEdge = edgesArrayList.get(i);
				source = g.getSource(currentEdge);
				dest = g.getDest(currentEdge);
				System.out.println(createARoad(currentEdge, source, dest));
			}
		}
		
		public static String createSNode(int nodeId){
			String node = "<node id=\"n" + nodeId + "\">"
						+ "<data key=\"d3\">S</data>"
						+ "</node>";
			return node;
		}
		
		public static String createLNode(int nodeId){
			String node = "<node id=\"n" + nodeId + "\">"
						+ "<data key=\"d3\">L</data>"
						+ "</node>";
			return node;
		}
		
		public static String createTransitionNode(int nodeId){
			String node = "	<node id=\"n" + nodeId + "\"/>";
			return node;		
		}
		
		public static String createMRoad(int id1, int id2, int source, int target){
			String firstEdge = "<edge id=\"" + id1 + "\" source=\"" 
						+ source + "\" target=\"" + target + "\">"
						+ "<data key=\"d2\">M</data>"
						+ "</edge>";
			String secondEdge = "<edge id=\"" + id2 + "\" source=\"" 
						+ target + "\" target=\"" + source + "\">"
						+ "<data key=\"d2\">M</data>"
						+ "</edge>";
			String fullEdge = firstEdge + "\n" + secondEdge;
			return fullEdge;		
		}
		
		public static String createARoad(int id, int source, int target){
			String edge = "<edge id=\"A"+ id + "\" source=\"n" + source + "\" target=\"n" + target + "\"/>";
			return edge;		
		}
		
		public static void buildGraph(int numberOfVertices, int sparsity){
			
			Collection<Integer> vertices;
			List<Integer> verticesArrayList;
			Random rnd = new Random();
			Integer selectedNode;
			Integer selectedNode2;
			int edgeCount = 2;
			
			g = new DirectedSparseMultigraph<Integer, Integer>();
			
			g.addVertex((Integer) 0);
			g.addVertex((Integer) 1);
			g.addEdge((Integer) 0, 0, 1);
			g.addEdge((Integer) 1, 1, 0);
			
			for(int j = 0; j < numberOfVertices-2; j++){
				//Create new vertex.
				g.addVertex((Integer) j+2);
					
				//Select an existing vertex at random.
				vertices = g.getVertices();
				verticesArrayList = new ArrayList<Integer>(vertices);
				selectedNode = verticesArrayList.get(rnd.nextInt(verticesArrayList.size()));
					
				//Add edge between the two nodes.
				g.addEdge((Integer) edgeCount, j+2, selectedNode);
				g.addEdge((Integer) edgeCount+1, selectedNode, j+2);
				edgeCount+=2;
			}
						
			//Randomly add edges according to sparsity parameter.		
			for(int i = 0; i < sparsity; i++){
			
				//Select two existing vertices at random.
				vertices = g.getVertices();
				verticesArrayList = new ArrayList<Integer>(vertices);
				selectedNode = verticesArrayList.get(rnd.nextInt(verticesArrayList.size()));
				selectedNode2 = verticesArrayList.get(rnd.nextInt(verticesArrayList.size()));
				
				//Add an edge between them
				g.addEdge(edgeCount, selectedNode, selectedNode2);
				g.addEdge(edgeCount+1, selectedNode2, selectedNode);
				edgeCount += 2;
			}
						
			
			
			//Code for connecting subgraphs.
			/*
			for(int i = 0; i<subgraphList.size()-1; i++){
				subgraph1 = subgraphList.get(i);
				subgraph2 = subgraphList.get(i+1);
				
				for(int j = 0; j < 2; j++){
					//Select an existing vertex at random from subgraph1.
					vertices = subgraph1.getVertices();
					verticesArrayList = new ArrayList<Integer>(vertices);
					selectedNode = verticesArrayList.get(rnd.nextInt(verticesArrayList.size()));
				
					//Select an existing vertex at random from subgraph2.
					vertices = subgraph2.getVertices();
					verticesArrayList = new ArrayList<Integer>(vertices);
					selectedNode2 = verticesArrayList.get(rnd.nextInt(verticesArrayList.size()));
					
					//Add edge between the two nodes.
					g.addEdge((Integer) edgeCount, selectedNode, selectedNode2);
					g.addEdge((Integer) edgeCount+1, selectedNode2, selectedNode);
					edgeCount+=2;
				}
			} */
					
		}
		
		public static void categoriseNodes(){
			Collection<Integer> vertices;
			Collection<Integer> edges;
			List<Integer> verticesArrayList;
			List<Integer> edgesArrayList;
			Integer currentNode;
			int highestDegree = 0;
			
			vertices = g.getVertices();
			verticesArrayList = new ArrayList<Integer>(vertices);
			
			int nodeDegreeArray[] = new int[verticesArrayList.size()];
			
			for(int i = 0; i < verticesArrayList.size(); i++){
				currentNode = verticesArrayList.get(i);
				edges = g.getInEdges(currentNode);
				edgesArrayList = new ArrayList<Integer>(edges);
				nodeDegreeArray[currentNode] = edgesArrayList.size();
			}
			
			for(int i = 0; i < nodeDegreeArray.length; i++){
				if(highestDegree < nodeDegreeArray[i]){
					highestDegree = nodeDegreeArray[i];
				}
			}
			
			for(int i = 0; i < nodeDegreeArray.length; i++){
				if(nodeDegreeArray[i] > highestDegree-4){
					lNodeList.add(i);
				}
				else{
					sNodeList.add(i);
				}
			}
			
			
		}
}

