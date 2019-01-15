package edu.asu.emit.algorithm.graph.shortestpaths;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.Pair;

public class ModifiedDijkstraShortestPathAlg implements BaseDijkstraShortestPathAlg{
	// Custom comparator to PriorityQueue
		Comparator<Pair<BaseVertex, Integer>> weightComparator = new Comparator<Pair<BaseVertex, Integer>>() {
	        public int compare(Pair<BaseVertex, Integer> v1, Pair<BaseVertex, Integer> v2) {
	            return v1.first().getWeight() - v2.first().getWeight();
	        }
	    };
	

	// Input
	private final BaseGraph graph;
	private Map<Edge, int[]> load;
	private int startTime;

	// Intermediate variables
	private Set<BaseVertex> determinedVertexSet = new HashSet<BaseVertex>();
	private Map<BaseVertex, Integer> startVertexDistanceIndex = new HashMap<BaseVertex, Integer>();
	private PriorityQueue<Pair<BaseVertex, Integer>> vertexCandidateQueue = 
			new PriorityQueue<Pair<BaseVertex, Integer>>(weightComparator);
	private Map<BaseVertex, BaseVertex> predecessorIndex = new HashMap<BaseVertex, BaseVertex>();
	private Set<Edge> determinedEdges = new HashSet<Edge>();

	/**
	 * Default constructor.
	 * @param graph
	 */
	public ModifiedDijkstraShortestPathAlg(final BaseGraph graph) {
        this.graph = graph;
	}

	/**
	 * Clear intermediate variables.
	 */
	public void clear()	{
		determinedVertexSet.clear();
		vertexCandidateQueue.clear();
		startVertexDistanceIndex.clear();
		predecessorIndex.clear();
		determinedEdges.clear();
	}

	/**
	 * Getter for the distance in terms of the start vertex
	 * 
	 * @return
	 */
	public Map<BaseVertex, Integer> getStartVertexDistanceIndex() {
        return startVertexDistanceIndex;
	}

	/**
	 * Getter for the index of the predecessors of vertices
	 * @return
	 */
	public Map<BaseVertex, BaseVertex> getPredecessorIndex() {
        return predecessorIndex;
	}

	/**
	 * Construct a tree rooted at "root" with 
	 * the shortest paths to the other vertices.
	 * 
	 * @param root
	 */
	public void getShortestPathTree(BaseVertex root) {
        determineShortestPaths(root, null, true);
	}
	
	/**
	 * Construct a flower rooted at "root" with 
	 * the shortest paths from the other vertices.
	 * 
	 * @param root
	 */
	public void getShortestPathFlower(BaseVertex root) {
        determineShortestPaths(null, root, false);
	}
	
	protected boolean allAdjacentEdgesUsed(BaseVertex vertex, boolean isSource2sink) {
		Set<BaseVertex> neighborVertexList = isSource2sink ?
				graph.getAdjacentVertices(vertex) : graph.getPrecedentVertices(vertex);
		Set<Edge> edgesToNeighbors = new HashSet<Edge>();
		for (BaseVertex neighbor : neighborVertexList) {
			if (determinedVertexSet.contains(neighbor)) continue;									// consider determined vertices separately to determined edges
			Edge edge = isSource2sink ? new Edge(vertex, neighbor) : new Edge(neighbor, vertex);
			edgesToNeighbors.add(edge);
		}
//		System.out.println("Tu:");
//		System.out.println(edgesToNeighbors);
		return determinedEdges.containsAll(edgesToNeighbors);
	}
	
	/**
	 * Do the work
	 */
	protected void determineShortestPaths(BaseVertex sourceVertex,
                                          BaseVertex sinkVertex, boolean isSource2sink)	{
		// 0. clean up variables
		clear();
		
		// 1. initialize members
		BaseVertex endVertex = isSource2sink ? sinkVertex : sourceVertex;
		BaseVertex startVertex = isSource2sink ? sourceVertex : sinkVertex;
		startVertexDistanceIndex.put(startVertex, 0);
		startVertex.setWeight(0);
		Pair<BaseVertex, Integer> element = new Pair<BaseVertex, Integer>(startVertex, 0);
		vertexCandidateQueue.add(element);

		// 2. start searching for the shortest path
		while (!vertexCandidateQueue.isEmpty()) {
			Pair<BaseVertex, Integer> curCandidatePair = vertexCandidateQueue.poll();
			BaseVertex curCandidateVertex = curCandidatePair.first();
			
//			System.out.println("curCandidatePair: " + curCandidatePair);
//			Set<BaseVertex> neighborVertexList = isSource2sink ?
//					graph.getAdjacentVertices(curCandidateVertex) : graph.getPrecedentVertices(curCandidateVertex);
//			System.out.print("Neighbors: ");
//			System.out.println(neighborVertexList);
//			for (BaseVertex neighbor : neighborVertexList) {
//				System.out.print(determinedVertexSet.contains(neighbor) + " ");
//			}
//			System.out.println();
//			System.out.print("Currently determined Edges: ");
//			System.out.println(determinedEdges);
			
			if (curCandidateVertex.equals(endVertex)) {
                break;
            }

			updateVertex(curCandidatePair, isSource2sink);
		}
	}

	/**
	 * Update the distance from the source to the concerned vertex.
	 * @param vertex
	 */
	private void updateVertex(Pair<BaseVertex, Integer> pair, boolean isSource2sink)	{							
		// 1. get the neighboring vertices 
		Set<BaseVertex> neighborVertexList = isSource2sink ?
			graph.getAdjacentVertices(pair.first()) : graph.getPrecedentVertices(pair.first());
			
		// 2. update the distance passing on current vertex
		for (BaseVertex curAdjacentVertex : neighborVertexList) {
			// 2.1 skip if visited before
			if (determinedVertexSet.contains(curAdjacentVertex)) {									// || edge(vertex, curAdjacentVertex) is saturated in
																									// any timeUnit between in the interval 
																									// [startTime + vertex.getWeight(), 
																									// 		startTime + vertex.getWeight() 
																									// + edge(vertex, curAdjacentVertex).getWeight)
                continue;
            }
			
			
			Edge edge = isSource2sink ? 
					new Edge(pair.first(), curAdjacentVertex)
					: new Edge(curAdjacentVertex, pair.first());
			int start = getStartTime() + pair.second();												// getStartTime() == queryTime, vertex.getWeight == relative dijkstraTime
																									// (relative time starting from 0 when vertex == source)
			int edgeWeight = isSource2sink ? graph.getEdgeWeight(pair.first(), curAdjacentVertex)			
					: graph.getEdgeWeight(curAdjacentVertex, pair.first());							// alternative option is to change vertexWeight, i.e., to start from source 
			int finish = start + edgeWeight;														// with time queryTime instead of 0
			
			
			if (determinedEdges.contains(edge)) continue;											// we have already went through the current edge, there is no need 
																									// to do this again
//			System.out.println("To: " + curAdjacentVertex);
//			System.out.println("TimeFrame: " + start + " " + finish);
			
			boolean cnt = false;
			if (load != null && load.containsKey(edge)) {											// there was some traffic
				for (int time = start; time < finish; ++time) { 
					if (load.get(edge).length > time && 
							load.get(edge)[time] >= graph.getEdgeCapacity(edge.first(), edge.second())) // if edge is saturated we cannot use it again
					cnt = true;
				}
			}
			if (cnt) continue;
			
			// 2.2 calculate the new distance
			int distance = pair.second();															// fixed -- Graph.DISCONNECTED should never happen
			
			distance += isSource2sink ? graph.getEdgeWeight(pair.first(), curAdjacentVertex)		// this is OK, since we know that the edge is in the graph
					: graph.getEdgeWeight(curAdjacentVertex, pair.first());
			
//			System.out.println("update");
//			if (startVertexDistanceIndex.containsKey(curAdjacentVertex))
//				System.out.println(startVertexDistanceIndex.get(curAdjacentVertex));
//			else {
//				System.out.println("Does not include");
//			}
			determinedEdges.add(edge);
			
			// 2.3 update the distance if necessary
			if (!startVertexDistanceIndex.containsKey(curAdjacentVertex)
			|| startVertexDistanceIndex.get(curAdjacentVertex) > distance) {
				startVertexDistanceIndex.put(curAdjacentVertex, distance);

				predecessorIndex.put(curAdjacentVertex, pair.first());
				
				curAdjacentVertex.setWeight(distance);
				vertexCandidateQueue.add(new Pair<BaseVertex, Integer>(curAdjacentVertex, distance));
			}
		}
		
//		System.out.println("updateVertex::");
//		System.out.println(pair.first());
		if (allAdjacentEdgesUsed(pair.first(), isSource2sink)) {
			determinedVertexSet.add(pair.first());
		}
		else {
			vertexCandidateQueue.add(new Pair<BaseVertex, Integer>(pair.first(), pair.second() + 1));
		}
	}
	
	/**
	 * Note that, the source should not be as same as the sink! (we could extend 
	 * this later on)
	 *  
	 * @param sourceVertex
	 * @param sinkVertex
	 * @return
	 */
	public Path getShortestPath(BaseVertex sourceVertex, BaseVertex sinkVertex)	{
		determineShortestPaths(sourceVertex, sinkVertex, true);
		//
		List<BaseVertex> vertexList = new Vector<BaseVertex>();
		List<Integer> arrivalTimes = new Vector<Integer>();
		int weight = startVertexDistanceIndex.containsKey(sinkVertex) ?								// OK -- covers situation where sink is unreachable 
			startVertexDistanceIndex.get(sinkVertex) : Graph.DISCONNECTED;
		if (weight != Graph.DISCONNECTED) {
			BaseVertex curVertex = sinkVertex;
			while(curVertex != null) {																// fixed -- while instead of do .. while
				vertexList.add(curVertex);
				arrivalTimes.add(getStartTime() + curVertex.getWeight());							// we keep absolute time (i.e., starting from queryTime)
				curVertex = predecessorIndex.get(curVertex);
			} 
			Collections.reverse(vertexList);
			Collections.reverse(arrivalTimes);
		}
		return new Path(vertexList, arrivalTimes);
	}
	
	//only for debugging purposes
	public Map<Edge, int[]> getLoad() {										
		return load;
	}

	public void setLoad(Map<Edge, int[]> load) {
		this.load = load;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	

}
