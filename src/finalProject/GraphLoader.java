package finalProject;

import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.io.graphml.*;
import java.io.*;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.*;

public class GraphLoader{
		
	static String src = "src/finalProject/descriptions.graphml";
	static String type;

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
                            	e.setWeight(40.0);
                            	e.setThreshold(100);
                            	e.setCapacity(150);
                            	e.setNumUsers(0);
                            }
                            else if(type.equals("A")){
                            	e.setWeight(60.0);
                            	e.setThreshold(70);
                            	e.setCapacity(100);
                            	e.setNumUsers(0);
                            }
                         //   e.setWeight(Double.parseDouble(emd.getProperty("d1")));   
                         //   e.setThreshold(Double.parseDouble(emd.getProperty("d4")));
                         //   e.setCapacity(Integer.parseInt(emd.getProperty("d6")));
                         //   e.setNumUsers(Integer.parseInt(emd.getProperty("d5")));
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
