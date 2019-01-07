package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.graph.shortestpaths.ModifiedDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;

public class DijkstraBenchmark {
	protected MyVariableGraph graph;
	protected Map<Edge, int[]> load;																// for each (edge, time) we keep traffic load
	protected BaseDijkstraShortestPathAlg dijkstra;
	
	protected Map<EdgeTime, Map<Path, Integer>> listOfPaths;										// Map<Path, Integer> is a replacement for multiset data structure
	
	public DijkstraBenchmark(MyVariableGraph graph) {														
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, Map<Path, Integer>>();
		dijkstra = new ModifiedDijkstraShortestPathAlg(graph);
	}
	
	public DijkstraBenchmark(MyVariableGraph graph, Map<Edge, int[]> load) {									
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
				EdgeTime edgeTime = new EdgeTime(edge, t + j);
				updateListOfPaths(edgeTime, path, isRemoved);
			}
			//update load map
			load.put(edge, timeMap);
			//update timeInstance
			t += edgeWeight;
		}
	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries) {
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query: queries) {
			int startTime = query.getStartTime();
			
			graph.useWholeGraph(true);
			Path shortestPath = processQuery(query, false);
//			System.out.println("SP::");
//			System.out.println(shortestPath);
			
			graph.clearAvailableEdges();
			for (int i = 0; i < shortestPath.size() - 1; ++i) {
				Edge edge = new Edge(shortestPath.get(i), shortestPath.get(i + 1));
				graph.addAvailableEdge(edge);
			}
			graph.useWholeGraph(false);
			Path benchmarkPath = processQuery(query, true);
//			System.out.println("BP::");
//			System.out.println(benchmarkPath);
			
			if (benchmarkPath.size() > 0) {
				updateLoad(benchmarkPath, startTime, false);
			}
			result.add(new Pair<Query, Path> (query, benchmarkPath));
		}
		
		return result;
	}
	
	public Path processQuery(Query query, boolean capacityAware) {
		if (capacityAware) dijkstra.setLoad(load);													 
		else dijkstra.setLoad(null);																
		
		dijkstra.setStartTime(query.getStartTime());												// Dijkstra must know when query happened, to be able to access correct
																									// load information		
		Path path = dijkstra.getShortestPath(query.first(), query.second());		
		
		return path;
	}
}
