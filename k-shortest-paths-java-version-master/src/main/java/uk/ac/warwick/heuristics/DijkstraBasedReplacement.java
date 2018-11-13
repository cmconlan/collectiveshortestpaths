package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import uk.ac.warwick.settings.Settings;

import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;

public class DijkstraBasedReplacement extends SequentialDijkstra {
	
	Set<EdgeTime> problematicEdges;
	
	public DijkstraBasedReplacement(Graph graph) {
		super(graph);
		problematicEdges = new TreeSet<EdgeTime>();
	}
	
	
	public List<Pair<Integer, Path>> process(Map<Integer, Query> queries, int startTime) {
		boolean capacityAware = false;
		List<Pair<Integer, Path>> result = super.process(queries, capacityAware);					// at first we want to calculate all the shortest paths
																									// ignoring capacities
		
		Map<Path, Set<Integer>> path2queries = new HashMap<Path, Set<Integer>>();					// we need to be able to figure out which query solves our resulting path					
		for (Pair<Integer, Path> pair : result) {													// 2 identical paths issue solved by queriesSet
			Set<Integer> queriesSet = path2queries.get(pair.second());
			if (queriesSet == null) {
				queriesSet = new HashSet<Integer>();
			}
			queriesSet.add(pair.first());
			path2queries.put(pair.second(), queriesSet);
		}
		
		// we need to identify edgeTime violating our capacity constraints:
		
		
		for (Edge edge : load.keySet()) {															// iterate over all edges (that we used before)
			for (int t = startTime; t < load.get(edge).length; ++t) {
				if (load.get(edge)[t] > graph.getEdgeCapacity(edge.first(), edge.second())) {
					EdgeTime edgeTime = new EdgeTime(edge, t);
//					listOfPaths.get(edgeTime);														// here are the Paths violating (edge, t) constraints
					problematicEdges.add(edgeTime);
				}
			}
		}
		
		for (EdgeTime edgeTime : problematicEdges) {
			while (load.get(edgeTime.first())[edgeTime.second()] 									
					> graph.getEdgeCapacity(edgeTime.first().first(), edgeTime.first().second())) {
				// create Heap h
				Set<Path> heap = new TreeSet<Path>();
				List<Path> pathsToReplace = new ArrayList<Path>();
				Map<Path, Path> new2old = new HashMap<Path, Path>();
				for (Path path : listOfPaths.get(edgeTime).keySet()) {
					 pathsToReplace.add(path);
				}
				
				if (Settings.DEBUG_LEVEL >= 2)
					System.out.println("Paths to replace: " + pathsToReplace);
				for (Path oldPath : pathsToReplace) {
					updateLoad(oldPath, startTime, true);
					Set<Integer> queriesIds = path2queries.get(oldPath);
					if (Settings.DEBUG_LEVEL >= 3) {
						System.out.println("OldPath: " + oldPath.getFirst() + " " + oldPath.getLast());
						System.out.println(queriesIds.isEmpty());
						System.out.println(queriesIds);
					}
					Iterator<Integer> queriesIdsIterator = queriesIds.iterator();
					int queryId = queriesIdsIterator.next();	
					Query query = queries.get(queryId);
					
					Path newPath = super.processQuery(query, startTime, true);
					
//					Another approach of choosing a path to replace
//					(instead of the shortest, we choose the smallest difference 
//					to the shortest path without traffic load
//					to use second approach uncomment 3 lines below
//					and change Path.compareTo function to Path.compareToDelta
//
					double delta = newPath.getWeight() - oldPath.getWeight();
					delta /= oldPath.getWeight();
					newPath.setDelta(delta);
					
					if (newPath.size() > 0) {
						heap.add(newPath);
						new2old.put(newPath, oldPath);
					}
					
					updateLoad(oldPath, startTime, false);											// restoring former state of the world
				}
				
				//empty heap case
				
				if (heap.isEmpty()) {																// we haven't found any good candidate
					if (Settings.DEBUG_LEVEL >= 1)
						System.out.println("Couldn't find any good replacement for " + 
								edgeTime.first() + " at time " + edgeTime.second());
					
																									//lets just pick a random path and replace it with empty path					
					Path oldPath = pathsToReplace.get(0);
					Path newPath = new Path();
					newPath.setWeight(Graph.DISCONNECTED);
					
					int queryId = switchPaths(oldPath, newPath, startTime, path2queries);
					result.set(result.indexOf(new Pair<Integer, Path>(queryId, oldPath)), 
							new Pair<Integer, Path>(queryId, newPath));	
					if (Settings.DEBUG_LEVEL >= 5) {
						System.out.println("\n\n\n\n\n\n");
						
						System.out.println("Decided to replace queryId = " + queryId + 
								" with an empty path.");
						
						System.out.println("\n\n\n\n\n\n");
					}
					break; 
				}															
				
				Iterator<Path> heapIterator = heap.iterator();
				
				//heap is non-empty hence iterator hasNext()
				
				Path newPath = heapIterator.next();
				Path oldPath = new2old.get(newPath);
				
				int queryId = switchPaths(oldPath, newPath, startTime, path2queries);
				result.set(result.indexOf(new Pair<Integer, Path>(queryId, oldPath)), 
						new Pair<Integer, Path>(queryId, newPath));
				
				if (Settings.DEBUG_LEVEL >= 5) {
					System.out.println("\n\n\n\n\n\n");
					
					System.out.println("QUERY_ID = " + queryId);
					System.out.println(oldPath);
					System.out.println("\n\n");
					System.out.println(newPath);
					
					System.out.println("\n\n\n\n\n\n");
				}
			}
		}
		
		
		// our result 
		return result;
	}
	
	/**
	 * Function updates traffic load i.e., erase oldPath and add newPath to the traffic load data structure
	 * and returns queryId that is fixing 
	 * @param oldPath
	 * @param newPath
	 * @param startTime
	 * @param path2queries
	 * @return
	 */
	public int switchPaths(Path oldPath, Path newPath, int startTime, Map<Path, Set<Integer>> path2queries) {	// [TODO] think about better name for this function
		//update state of the world (i.e., replace those paths in our traffic load)
		updateLoad(oldPath, startTime, true);
		if (newPath.size() > 0)
			updateLoad(newPath, startTime, false);
		
		Set<Integer> queriesIds = path2queries.get(oldPath);
		Iterator<Integer> queriesIdsIterator = queriesIds.iterator();
		int queryId = queriesIdsIterator.next();
		//remove our paths from path2queries set													//should work since we can't replace the same path twice
		queriesIdsIterator.remove();
		return queryId;
	}

	public static void main(String[] args) {
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		Graph graph = new Graph(graphPath);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		
		int startTime = 0;
		DijkstraBasedReplacement dbr = new DijkstraBasedReplacement(graph);
		List<Pair<Integer, Path>> queriesWithSolutions = dbr.process(queryHandler.getQueries(), startTime);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			int queryId = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			System.out.println("QueryId = " + queryId +
					" {" + queryHandler.getQuery(queryId).first() + ", " + queryHandler.getQuery(queryId).second() + "}" +
					" Solution = " + solution);
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(dbr.evaluate(paths));
		dbr.showLoad();
		
		System.out.println(dbr.listOfPaths);

	}

}
