package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.shortestpaths.DijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;
import edu.asu.emit.algorithm.utils.Query;


/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * this class is first heuristic to solve collective shortest path optimization problem
 */
public class SequentialDijkstra {
	
	protected Graph graph;
	protected Map<Edge, int[]> load;																// for each (edge, time) we keep traffic load
	protected DijkstraShortestPathAlg dijkstra;
	
	protected Map<EdgeTime, List<Path>> listOfPaths;												// necessary for DijkstraBasedReplacement
	
	public SequentialDijkstra(Graph graph) {														// TODO read load from file
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, List<Path>>();
		dijkstra = new DijkstraShortestPathAlg(graph);
	}
	
	protected void updateListOfPaths(EdgeTime edgeTime, Path path) {
		List<Path> paths = listOfPaths.get(edgeTime);
		if (paths == null) 
			paths = new ArrayList<Path>(); 
		paths.add(path);
		listOfPaths.put(edgeTime, paths);
	}
	
	protected void updateLoad(Path p, int startTime, boolean isRemoved) {							// path can be either added or removed
		int t = startTime;
		for (int i = 0; i < p.size() - 1; ++i) {
			Edge edge = new Edge(p.get(i), p.get(i + 1));
			int edgeWeight = graph.getEdgeWeight(p.get(i), p.get(i + 1));
			int[] timeMap;
			
			if (load.containsKey(edge)) {
				timeMap = load.get(edge);
				
				if (timeMap.length < t + edgeWeight) {												
					timeMap = Arrays.copyOf(timeMap, t + edgeWeight);								
				}
			} else {
				timeMap = new int[t + edgeWeight];  												// we need to have loads at least till t + edgeWeight
			}
			//increase traffic load where necessary
			for (int j = 0; j < edgeWeight; ++j) {
				if (isRemoved)
					timeMap[t + j]--;
				else {
					timeMap[t + j]++;
					EdgeTime edgeTime= new EdgeTime(edge, t + j);
					System.out.println(edgeTime + " " + p);
					
					updateListOfPaths(edgeTime, p);
				}
			}
			//update load map
			load.put(edge, timeMap);
			//update timeInstance
			t += edgeWeight;
		}
	}
	
	
	public List<Pair<Query, Path>> process(List<Query> queries, int startTime, boolean capacityAware) {
// 		redundant part (moved to processQuery)
//		if (capacityAware) dijkstra.setLoad(load);													// dijkstra.load is a reference to SequentialDijkstra.load 
//		else dijkstra.setLoad(null);																// (both point to the same object)
		
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query : queries) {
			Path path = processQuery(query, startTime, capacityAware);
			if (path.size() > 0){
				System.out.println("Algorithm found a path from "
						+ query.first() + " to " + query.second());
				updateLoad(path, startTime, false);													// it automatically updates dijkstra.load
			}
			else {
				System.out.println("Algorithm failed to find a path from " 
						+ query.first() + " to " + query.second());
			}
			result.add(new Pair<Query, Path> (query, path));
		}
		return result;																				// our result variable contains failed queries
	}
	
	public Path processQuery(Query query, int startTime, boolean capacityAware) {
		if (capacityAware) dijkstra.setLoad(load);													// dijkstra.load is a reference to SequentialDijkstra.load 
		else dijkstra.setLoad(null);																// (both point to the same object)
		
		Path path = dijkstra.getShortestPath(query.first(), query.second());		
		
		return path;
	}

	
	public void showLoad() {
		System.out.println("Load::");
		for (Edge edge : load.keySet()) {
			System.out.print(edge + " : ");
			for (int t = 0; t < load.get(edge).length; ++t) {
				System.out.print("(time = " + t + ", load = "  + load.get(edge)[t] + ", " +
						"capacity = " + graph.getEdgeCapacity(edge.first(), edge.second()) + "), ");
			}
			System.out.println();
		}
	}
	
	// metric for evaluating scores
	/**
	 * 
	 * @return number of failed Paths, plus totalTravelTime of feasible paths
	 */
	public Pair<Integer, Integer> evaluate(List<Path> paths) {
		int nFailedPaths = 0;
		int totalTravelTime = 0;
		
		for (Path path : paths) {
			if (path.getWeight() == Graph.DISCONNECTED) {
				nFailedPaths++;
			} else {
				totalTravelTime += path.getWeight();												// consider double/long long
			}
		}
		return new Pair<Integer,Integer>(nFailedPaths, totalTravelTime);
	}
	
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		Graph graph = new Graph(graphPath);
		SequentialDijkstra seqDijkstra = new SequentialDijkstra(graph); 							
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		
		int startTime = 0;
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), startTime, capacityAware);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			paths.add(queriesWithSolutions.get(i).second());
			System.out.println("Query = " + queriesWithSolutions.get(i).first() + 
					" Solution = " + queriesWithSolutions.get(i).second());
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		seqDijkstra.showLoad();
		
		System.out.println(seqDijkstra.listOfPaths);

		
	}

}
