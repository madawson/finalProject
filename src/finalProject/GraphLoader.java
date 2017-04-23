package finalProject;

import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.io.graphml.*;
import java.io.*;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.*;

/**
 * @author      Matthew Dawson 
 * @version     1.0                 (current version number of program)
 * @since       1.0          (the version of the package this class was first added to)
 */

public class GraphLoader{
	
	/**
	 * The location of the graphml formatted file to be imported.
	 */
	static String src = "src/finalProject/descriptions.graphml";
	
	/**
	 * Used to store the type of current edge being imported; either "M" or "A".
	 */
	static String type;

	/**
	 * Attempts to import a graph from a graphml formatted file in a specified location.
	 * @return an object of type "DirectedSparseMultigraph", provided by JUNG.
	 */
	public static DirectedSparseMultigraph<MyNode,MyEdge> importGraph(){
			        	
		DirectedSparseMultigraph<MyNode,MyEdge> g = new DirectedSparseMultigraph<MyNode,MyEdge>();
	    
		try{
			//Create a new file reader.
	    	Reader reader = new FileReader(src);
	    	
            //Graph Transformer
            Transformer<GraphMetadata,DirectedSparseMultigraph<MyNode,MyEdge>> graphTransformer = new Transformer<GraphMetadata,DirectedSparseMultigraph<MyNode,MyEdge>>(){
                public DirectedSparseMultigraph<MyNode,MyEdge> transform(GraphMetadata gmd){
                        return new DirectedSparseMultigraph<MyNode,MyEdge>();
                }
            };
            
            //Vertex Transformer
            Transformer<NodeMetadata,MyNode> vertexTransformer = new Transformer<NodeMetadata,MyNode>(){
                    public MyNode transform(NodeMetadata nmd){
                            MyNode n = new MyNode() ;
                            n.setType(nmd.getProperty("d3"));
                            n.setId(nmd.getId());
                            return n;
                    }
            };
            
            //Edge Transformer
            Transformer<EdgeMetadata,MyEdge> edgeTransformer = new Transformer<EdgeMetadata,MyEdge>(){
                    public MyEdge transform(EdgeMetadata emd){
                            MyEdge e = new MyEdge();
                            e.setId(emd.getId());
                            type = emd.getProperty("d2");
                            e.setType(type);
                            if(type.equals("M")){
                            	e.setInitialWeight(20.0);
                            	e.setWeight(20.0);
                            	e.setThreshold(20);
                            	e.setCapacity(25);
                            	e.setNumUsers(0);
                            	e.setInitialProgressRate(10.0);
                            }
                            else if(type.equals("A")){
                            	e.setInitialWeight(80.0);
                            	e.setWeight(80.0);
                            	e.setThreshold(15);
                            	e.setCapacity(20);
                            	e.setNumUsers(0);
                            	e.setInitialProgressRate(5.0);
                            }
                            return e;
                    }
            };
            
            //Hyperedge Transformer
            Transformer<HyperEdgeMetadata, MyEdge> hyperEdgeTransformer = new Transformer<HyperEdgeMetadata,MyEdge>(){
                    public MyEdge transform(HyperEdgeMetadata emd){
                            MyEdge e = new MyEdge();
                            e.setWeight(Double.parseDouble(emd.getProperty("d1")));
                            return e;
                    }
            };
            
            //Create GraphMLReader2 object
            GraphMLReader2<DirectedSparseMultigraph<MyNode,MyEdge>,MyNode,MyEdge> graphreader =
                    new GraphMLReader2<DirectedSparseMultigraph<MyNode,MyEdge>,MyNode,MyEdge>(
                                    reader,
                                    graphTransformer,
                                    vertexTransformer,
                                    edgeTransformer,
                                    hyperEdgeTransformer);
            
            //Load graph
            g = graphreader.readGraph();
            return g;
	       }
	    catch(FileNotFoundException e){
	    	System.out.println(e);
	    	System.out.println("File not found. Terminating...");
	    	System.exit(1);
	       }
	    catch(GraphIOException e){
        	System.out.println(e);
        	System.out.println("Graph IO Exception. Terminating...");
        	System.exit(1);	    	
	    }
	    
		System.out.println("Something has gone wrong. End of importGraph() method reached...");
		return null;
	    
	}
}
