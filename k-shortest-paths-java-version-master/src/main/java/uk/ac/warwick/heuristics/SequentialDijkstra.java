package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import uk.ac.warwick.settings.Settings;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.shortestpaths.DijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;


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
	
	protected Map<EdgeTime, Map<Path, Integer>> listOfPaths;										// necessary for DijkstraBasedReplacement
	
	public SequentialDijkstra(Graph graph) {														
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, Map<Path, Integer>>();									// I'd prefer TreeMap but it doesn't work because of vertex.compareTo
																									// that is used in Dijkstra (i.e., compares weights) not ids -- fixed
																									// Map<Path, Integer> is a replacement for multiset data structure
		dijkstra = new DijkstraShortestPathAlg(graph);
	}
	
	public SequentialDijkstra(Graph graph, Map<Edge, int[]> load) {									
		this(graph);																				// calling the other constructor
		this.load = load;
	}
	
	protected void updateListOfPaths(EdgeTime edgeTime, Path path, boolean isRemoved) {
		Map<Path, Integer> paths = listOfPaths.get(edgeTime);										// imitates multiset of Paths 
		if (paths == null) 
			paths = new TreeMap<Path, Integer>();
		
		if (!isRemoved) paths.put(path, 1);
		else {
			int cnt = paths.get(path);
			if (cnt == 1) paths.remove(path);
			else {
				paths.put(path, --cnt);
			}
		}
		
		listOfPaths.put(edgeTime, paths);
	}
	
	protected void updateLoad(Path path, int startTime, boolean isRemoved) {						// path can be either added or removed
		
		int t = startTime;
		for (int i = 0; i < path.size() - 1; ++i) {
			Edge edge = new Edge(path.get(i), path.get(i + 1));
			int edgeWeight = graph.getEdgeWeight(path.get(i), path.get(i + 1));
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
				if (isRemoved) {
					timeMap[t + j]--;																 
																																		
				}
				else {
					timeMap[t + j]++;
				}
				EdgeTime edgeTime= new EdgeTime(edge, t + j);
				updateListOfPaths(edgeTime, path, isRemoved);
			}
			//update load map
			load.put(edge, timeMap);
			//update timeInstance
			t += edgeWeight;
		}
	}
	
	
	public List<Pair<Query, Path>> processWithShift(Set<Query> queries, boolean capacityAware) {
		Set<Query> unresolvedQueries = new HashSet<Query>(queries);									// copy
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query,Path>>();
		
		while (!unresolvedQueries.isEmpty()) {
			List<Pair<Query, Path>> processResult = process(unresolvedQueries, capacityAware);
			unresolvedQueries.clear();																
			
			for (Pair<Query, Path> pair : processResult) {
				if (pair.second().getWeight() == Graph.DISCONNECTED) {
					Query oldQuery = pair.first();
					Query newQuery = new Query(oldQuery.first(), oldQuery.second(), oldQuery.getStartTime() + 1);
					newQuery.setInitialStartTime(oldQuery.getInitialStartTime());					// we need to remember initial query startTime
					unresolvedQueries.add(newQuery);
				}
				else {
					Query query = pair.first();
					Path path = pair.second();
					int waitingTime = query.getStartTime()- query.getInitialStartTime();
					path.setWeight(path.getWeight() + waitingTime);
					result.add(pair);
				}
			}
			
		}
		
		
		return result;
	}
	
	public List<Pair<Query, Path>> process(Set<Query> queries, boolean capacityAware) {
		
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query : queries) {
			int startTime = query.getStartTime();
			Path path = processQuery(query, capacityAware);
			if (path.size() > 0){
				if (Settings.DEBUG_LEVEL >= 1)
					System.out.println("Algorithm found a path from "
							+ query.first() + " to " + query.second());
				updateLoad(path, startTime, false);													// it automatically updates dijkstra.load
			}
			else {
				if (Settings.DEBUG_LEVEL >= 1)
				System.out.println("Algorithm failed to find a path from " 
						+ query.first() + " to " + query.second());
				
			}
			result.add(new Pair<Query, Path> (query, path));
		}
		return result;																				// our result variable contains failed queries
	}
	
	public Path processQuery(Query query, boolean capacityAware) {
		if (capacityAware) dijkstra.setLoad(load);													// dijkstra.load is a reference to SequentialDijkstra.load 
		else dijkstra.setLoad(null);																// (both point to the same object)
		
		dijkstra.setStartTime(query.getStartTime());												// Dijkstra must know when query happened, to be able to access correct
																									// load information		
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
				totalTravelTime += path.getWeight();												// [TODO] consider double/long long
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
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.processWithShift(queryHandler.getQueries(), capacityAware);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			Query query = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			System.out.println("QueryId = " + query.getId() +
					" {" + query.first() + ", " + query.second() + "}" +
					" Solution = " + solution);
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		seqDijkstra.showLoad();
		
		System.out.println(seqDijkstra.listOfPaths);

		
	}

}
