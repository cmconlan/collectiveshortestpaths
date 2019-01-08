package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import uk.ac.warwick.settings.Settings;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.shortestpaths.ModifiedDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;


/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * This class is first heuristic to solve collective shortest path optimization problem.
 * It simply assign a shortest path w.r.t to the given traffic load 
 * (caused by previous queries solutions) if it is possible.
 * If the algorithm does not find any feasible solution it assigns empty path of infinite weight.
 */
public class SequentialDijkstra extends AbstractSolution{
	static int debug = 0; 
	
//	protected BaseGraph graph;
//	protected Map<Edge, int[]> load;																// for each (edge, time) we keep traffic load
//	protected BaseDijkstraShortestPathAlg dijkstra;
//	
//	protected Map<EdgeTime, Map<Path, Integer>> listOfPaths;										// necessary for DijkstraBasedReplacement
//	
	public SequentialDijkstra(MyVariableGraph graph) {														
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, Map<Path, Integer>>();									// I'd prefer TreeMap but it doesn't work because of vertex.compareTo
																									// that is used in Dijkstra (i.e., compares weights) not ids -- fixed
																									// Map<Path, Integer> is a replacement for multiset data structure
		dijkstra = new ModifiedDijkstraShortestPathAlg(graph);
	}
	
	public SequentialDijkstra(MyVariableGraph graph, Map<Edge, int[]> load) {									
		this(graph);																				// calling the other constructor
		this.load = load;
	}
	
//	protected void updateListOfPaths(EdgeTime edgeTime, Path path, boolean isRemoved) {
//		Map<Path, Integer> paths = listOfPaths.get(edgeTime);										// imitates multiset of Paths 
//		if (paths == null) 
//			paths = new TreeMap<Path, Integer>();
//		
//		if (!isRemoved) paths.put(path, 1);
//		else {
//			int cnt = paths.get(path);
//			if (cnt == 1) paths.remove(path);
//			else {
//				paths.put(path, --cnt);
//			}
//		}
//		
//		listOfPaths.put(edgeTime, paths);
//	}
	
//	protected void updateLoad(Path path, int startTime, boolean isRemoved) {						// path can be either added or removed
//		
//		int t = startTime;
//		for (int i = 0; i < path.size() - 1; ++i) {
//			Edge edge = new Edge(path.get(i), path.get(i + 1));
//			int edgeWeight = graph.getEdgeWeight(path.get(i), path.get(i + 1));
//			int[] timeMap;
//			
//			if (load.containsKey(edge)) {
//				timeMap = load.get(edge);
//				
//				if (timeMap.length < t + edgeWeight) {												
//					timeMap = Arrays.copyOf(timeMap, t + edgeWeight);								
//				}
//			} else {
//				timeMap = new int[t + edgeWeight];  												// we need to have loads at least till t + edgeWeight
//			}
//			//increase traffic load where necessary
//			for (int j = 0; j < edgeWeight; ++j) {
//				if (isRemoved) {
//					timeMap[t + j]--;																 
//																																		
//				}
//				else {
//					timeMap[t + j]++;
//				}
//				EdgeTime edgeTime = new EdgeTime(edge, t + j);
//				updateListOfPaths(edgeTime, path, isRemoved);
//			}
//			//update load map
//			load.put(edge, timeMap);
//			//update timeInstance
//			t += edgeWeight;
//		}
//	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries, boolean capacityAware) {
		
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query : queries) {
			int startTime = query.getStartTime();
			Path path = processQuery(query, capacityAware);
			if (path.size() > 0){
				if (Settings.DEBUG_LEVEL >= 1)
					System.out.println("Algorithm found a path from "
							+ query.first() + " to " + query.second() + " at time " + 
							query.getStartTime() + " (" + query.getInitialStartTime() + ") " + debug);
				debug++;
				updateLoad(path, startTime, false);													// it automatically updates dijkstra.load
			}
			else {
				if (Settings.DEBUG_LEVEL >= 1)
				System.out.println("Algorithm failed to find a path from " 
						+ query.first() + " to " + query.second() + " at time " + 
						query.getStartTime() + " (" + query.getInitialStartTime() + ") " + debug);
				
			}
			result.add(new Pair<Query, Path> (query, path));
		}
		return result;																				// our result variable contains failed queries
	}
	
//	public Path processQuery(Query query, boolean capacityAware) {
//		if (capacityAware) dijkstra.setLoad(load);													// dijkstra.load is a reference to SequentialDijkstra.load 
//		else dijkstra.setLoad(null);																// (both point to the same object)
//		
//		dijkstra.setStartTime(query.getStartTime());												// Dijkstra must know when query happened, to be able to access correct
//																									// load information		
//		Path path = dijkstra.getShortestPath(query.first(), query.second());		
//		
//		return path;
//	}

	
	
	
	
	
	public int getMaxWaitingTime(List<Path> paths) {
		int maximumWaitingTime = 0;
		
		for (Path path : paths) {
			if (maximumWaitingTime < path.getWaitingTime())
				maximumWaitingTime = path.getWaitingTime();
		}
		return maximumWaitingTime;
	}
	
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		MyVariableGraph graph = new MyVariableGraph(graphPath);
		SequentialDijkstra seqDijkstra = new SequentialDijkstra(graph); 							
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), capacityAware);
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
		System.out.print("maximumWaitingTime = ");
		System.out.println(seqDijkstra.getMaxWaitingTime(paths));
		seqDijkstra.showLoad();
		
		System.out.println(seqDijkstra.listOfPaths);

		
	}

}
