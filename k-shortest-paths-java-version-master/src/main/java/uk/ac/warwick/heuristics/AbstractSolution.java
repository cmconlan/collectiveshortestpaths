package uk.ac.warwick.heuristics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;

public abstract class AbstractSolution implements BaseSolution{
	
	protected MyVariableGraph graph;
	protected Map<Edge, int[]> load;																// for each (edge, time) we keep traffic load
	protected BaseDijkstraShortestPathAlg dijkstra;
	
	protected Map<EdgeTime, Map<Path, Integer>> listOfPaths;	
	
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
}
