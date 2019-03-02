package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;

import cs455.overlay.dijkstra.ShortestPath.Vertex;
import cs455.overlay.node.Node;
import cs455.overlay.wireformats.MessagingNodesList.NodeLink;
//This code was aided by looking at code on this site : https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/

public class ShortestPath {
	private PriorityQueue<Vertex> queue; 
	private ArrayList<Vertex> vertList;
	private ArrayList<Edge> edges;
	private Vertex myVertex;

	public ShortestPath (ArrayList<NodeLink> mNodeList, String myIP, int myPort, int nNodes) {
		vertList = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();


		buildGraph(mNodeList);	//Creates lists, including adjacency matrix representing graph

		myVertex = getVert(myIP, myPort);
		myVertex.dist=0;
		queue = new PriorityQueue<>(new PathComparator()); 	//ideal path queue
		queue.add(myVertex);


		//Beginning search for best path 
		Vertex u, v;
		while(queue.size() > 0) {
			Vertex currentVert = queue.poll();
			//for all neighbors of current node
			for(int i = 0; i< currentVert.getAdjacentVertexes().size();i++ ) {
				u = currentVert;			//is initially myVertex (a.k.a. the vertex of the node that calls this class)
				v = currentVert.getAdjacentVertexes().get(i);
				int changedDist= u.dist + getEdge(u,v);
				if(changedDist < v.dist ) {
					v.setParent(u); 
					v.setDistance(changedDist); 
					queue.add(v);
				}
			}
		}
	}

	//Actually returns what path the node should send the message down 
	public Stack<String> getShortestPath(String vertexIP, int vertexPort) {
		Stack<String> rightDirection = new Stack<String>();
		Vertex endVertex = getVert(vertexIP, vertexPort);
		while (endVertex.parent !=null) {
			if(endVertex.parent!=null) {
				rightDirection.add(endVertex.ip);
				endVertex = endVertex.parent;
			}
		}
		return rightDirection;
	}

	// Transforms each element in the list of edges into proper edges and vertexes  
	private void buildGraph(ArrayList<NodeLink> mnodeList) {
		for(NodeLink mnode : mnodeList){
			int p = mnode.contactPort;
			String ip = mnode.contactIP;
			Vertex v1, v2;

			if(!containsVert(ip,p)){
				v1 = new Vertex(ip,p);
				vertList.add(v1);
			}else v1 = getVert(ip,p);

			String ip2 = mnode.ipAddress;
			int p2 = mnode.port;
			if(!containsVert(ip2,p2)){
				v2 = new Vertex(ip2,p2);
				vertList.add(v2);
			}else v2 = getVert(ip2,p2);
			//adds edge to official edge list 
			edges.add(new Edge(v1, v2, mnode.getLinkWeight()));
			v1.setAdjacencies(v2);
			v2.setAdjacencies(v1);
		}
	}

	// Returns true if a vertex with the given ip and port yet exists
	private boolean containsVert(String vip, int vp) {
		for(Vertex v : vertList) 
			if (v.ip.contentEquals(vip) && v.port ==vp) 
				return true;
		return false;
	}

	// Returns a vertex that matches the given ip and port number
	private Vertex getVert(String vip, int vp) {
		for(Vertex v : vertList) 
			if (v.ip.contentEquals(vip) && v.port ==vp) 
				return v;
		return null;
	}

	//provides a random node for message sending target
	public Vertex getRandomNode() {
		ArrayList<Vertex> vRandList = new ArrayList<Vertex>();
		for (Vertex v : getVertList()){
			if(!v.equalsVert(myVertex)) {
				vRandList.add(v);
			}
		}
		Random rand = new Random();
		Vertex randomNode = vRandList.get(rand.nextInt(vRandList.size()));

		return randomNode;
	}


	public void showShortestPaths() {
		for(Vertex endVertex: vertList) {
			Stack<Vertex> rightDirection = new  Stack<Vertex>();
			while (endVertex.parent !=null) {
				if(endVertex.parent!=null) {
					rightDirection.add(endVertex);
					endVertex = endVertex.parent;
				}else {
					System.out.println("The Message has reached its destination!");
				}
			}
			System.out.print(myVertex.ip);
			while(!rightDirection.isEmpty()) {
				Vertex x = rightDirection.pop();
				System.out.printf("--%d--%s", getEdge(x.parent, x), x.ip);
			}
			System.out.println();
		}
	}

	/* Graph's Vertex object */
	public class Vertex {
		public final String ip;
		public final int port;
		public boolean visted = false;
		private Vertex parent = null;
		private LinkedList<Vertex> adjacentTo;
		private int dist = Integer.MAX_VALUE;
		public Vertex (String vertexip, int vertexport) {
			ip = vertexip;
			port = vertexport;
			adjacentTo = new LinkedList<Vertex>();
		}
		public void setAdjacencies(Vertex buddy) {
			adjacentTo.add(buddy);
		}
		public void setParent(Vertex v) {
			parent = v;
		}
		public LinkedList<Vertex> getAdjacentVertexes(){
			return adjacentTo;
		}
		public void setDistance (int d) {
			dist = d;
		}

		public boolean equalsVert(Vertex otherVert) {
			if(this.ip.contentEquals(otherVert.ip) && (this.port ==otherVert.port)) {
				return true;}
			return false;
		}
	}

	/*Allows for the comparison of two vertexes in priority queue */
	class PathComparator implements Comparator<Vertex>{ 
		public int compare(Vertex v1, Vertex v2) { 
			if (v1.dist < v2.dist) return 1; 
			return 0; 
		}
	}

	/* Graph's edge object */
	private class Edge {
		public int weight;
		public Vertex origin;
		public Vertex target;
		public Edge(Vertex og, Vertex t, int w) {
			origin = og;
			target = t;
			weight = w;
		}
	}

	// Returns the weight of the edge between two vertexes
	private int getEdge(Vertex v1, Vertex v2) {
		for(Edge e : edges) 
			if ((e.origin.equalsVert(v1)&& e.target.equalsVert(v2))||(e.origin.equalsVert(v2)&& e.target.equalsVert(v1))) 
				return e.weight;
		return -1;
	}

	public ArrayList<Vertex> getVertList() {return vertList;}

}

