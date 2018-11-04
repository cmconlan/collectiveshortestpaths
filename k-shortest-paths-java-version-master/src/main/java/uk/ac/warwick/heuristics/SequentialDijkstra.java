package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.graph.shortestpaths.DijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Pair;


/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * this class is first heuristic to solve collective shortest path optimization problem
 */
public class SequentialDijkstra {
	
	private Graph graph;
	private List<Pair<BaseVertex, BaseVertex>> queries;
	private Map<Pair<BaseVertex, BaseVertex>, int[]> load;											// for each (edge, time) we keep traffic load
	
	
	public SequentialDijkstra(String graphPath, String queriesPath) {								// TODO read load from file
		graph = new Graph(graphPath);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		queries = queryHandler.getQueries();
		load = new HashMap<Pair<BaseVertex, BaseVertex>, int[]>();
	}
	
	private void updateLoad(Path p, int startTime) {
		int t = startTime;
		for (int i = 0; i < p.size() - 1; ++i) {
			Pair<BaseVertex, BaseVertex> edge = new Pair<BaseVertex, BaseVertex>(p.get(i), p.get(i+1));
			int edgeWeight = graph.getEdgeWeight(p.get(i), p.get(i + 1));
			int[] timeMap;
			
			if (load.containsKey(edge)) {
				timeMap = load.get(edge);
				
				if (timeMap.length < t + edgeWeight) {
					timeMap = Arrays.copyOf(timeMap, t + edgeWeight);								// TODO verify if it works properly
				}
			} else {
				timeMap = new int[t + edgeWeight];  												// we need to have loads at least till t + edgeWeight
			}
			//increase traffic load where necessary
			for (int j = 0; j < edgeWeight; ++j) {
				timeMap[t + j]++;
			}
			//update load map
			load.put(edge, timeMap);
			//update timeInstance
			t += edgeWeight;
		}
	}
	
	
	public List<Pair<Pair<BaseVertex, BaseVertex>, Path>> process() {
		DijkstraShortestPathAlg dijkstra = new DijkstraShortestPathAlg(graph);
		dijkstra.setLoad(load);																		// dijkstra.load is a reference to SequentialDijkstra.load 
																									// (both point to the same object)
		int startTime = 0;
		
		List<Pair<Pair<BaseVertex, BaseVertex>, Path>> result = new ArrayList<Pair<Pair<BaseVertex, BaseVertex>, Path>>();
		
		for (Pair<BaseVertex, BaseVertex> query : queries) {
			Path path = dijkstra.getShortestPath(query.first(), query.second());
			if (path.size() > 0){
				System.out.println("Algorithm found a path from "
						+ query.first() + " to " + query.second());
				updateLoad(path, startTime);														// it automatically updates dijkstra.load
			}
			else {
				System.out.println("Algorithm failed to find a path from " 
						+ query.first() + " to " + query.second());
			}
			result.add(new Pair<Pair<BaseVertex, BaseVertex>, Path> (query, path));
		}
		return result;																				// our result variable contains failed queries
	}

	
	public void showLoad() {
		System.out.println("Load::");
		for (Pair<BaseVertex, BaseVertex> edge : load.keySet()) {
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
			if (path.getWeight() == graph.DISCONNECTED) {
				nFailedPaths++;
			} else {
				totalTravelTime += path.getWeight();												// consider double/long long
			}
		}
		return new Pair<Integer,Integer>(nFailedPaths, totalTravelTime);
	}
	
	
	public static void main(String[] args) {
		
		SequentialDijkstra seqDijkstra = new SequentialDijkstra("data/graphs/graph1.txt", "data/queries/queries1.txt");
		List<Pair<Pair<BaseVertex, BaseVertex>, Path>> queriesWithSolutions = seqDijkstra.process();
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			paths.add(queriesWithSolutions.get(i).second());
			System.out.println("Query = " + queriesWithSolutions.get(i).first() + 
					" Solution = " + queriesWithSolutions.get(i).second());
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		seqDijkstra.showLoad();

		
	}

}
