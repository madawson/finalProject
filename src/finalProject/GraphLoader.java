package finalProject;

import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.io.graphml.*;
import java.io.*;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.*;

public class GraphLoader{
		
	static String src = "src/finalProject/descriptions.graphml";

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
                            MyEdge e = new MyEdge() ;
                            e.setWeight(Double.parseDouble(emd.getProperty("d1")));
                            e.setType(emd.getProperty("d2"));
                            e.setId(emd.getId());
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
