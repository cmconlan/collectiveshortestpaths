package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;
import edu.asu.emit.algorithm.utils.Query;

public class DijkstraBasedReplacement extends SequentialDijkstra {
	
	Set<EdgeTime> problematicEdges;
	
	public DijkstraBasedReplacement(Graph graph) {
		super(graph);
		problematicEdges = new TreeSet<EdgeTime>();
	}
	
	
	public List<Pair<Integer, Path>> process(Map<Integer, Query> queries, int startTime) {
		boolean capacityAware = false;
		List<Pair<Integer, Path>> result = super.process(queries, startTime, capacityAware);		// at first we want to calculate all the shortest paths
																									// ignoring capacities
		
		Map<Path, Set<Integer>> path2queries = new HashMap<Path, Set<Integer>>();					// we need to be able to figure out which query solves our resulting path					
		for (Pair<Integer, Path> pair : result) {													// [TODO] what if we have 2 identical paths
			Set<Integer> queriesSet = path2queries.get(pair.second());
			if (queriesSet == null) {
				queriesSet = new HashSet<Integer>();
			}
			queriesSet.add(pair.first());
			path2queries.put(pair.second(), queriesSet);
		}
		
		// we need to identify edgeTime violating our capacity constraints:
		
		
		for (Edge edge : load.keySet()) {
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
				for (Path path : listOfPaths.get(edgeTime)) {
					 pathsToReplace.add(path);
				}
				
				
				System.out.println("Path to replace: " + pathsToReplace);
				for (Path oldPath : pathsToReplace) {
					updateLoad(oldPath, startTime, true);
					Set<Integer> queriesIds = path2queries.get(oldPath);
					Iterator<Integer> queriesIdsIterator = queriesIds.iterator();
					int queryId = queriesIdsIterator.next();	
					Query query = queries.get(queryId);
					
					Path newPath = super.processQuery(query, startTime, true);
					if (newPath.size() > 0) {
						heap.add(newPath);
						new2old.put(newPath, oldPath);
					}
					
					updateLoad(oldPath, startTime, false);											// restoring former state of the world
				}
				
				//empty heap case
				
				if (heap.isEmpty()) {																// we haven't found any good candidate
					System.out.println("Couldn't find a good replacement for " + 
							edgeTime.first() + " at time " + edgeTime.second());
					
																									//lets just pick a random path and replace it with empty path					
					Path oldPath = pathsToReplace.get(0);
					Path newPath = new Path();
					newPath.setWeight(Graph.DISCONNECTED);
					
					int queryId = switchPaths(oldPath, newPath, startTime, path2queries);
					result.set(result.indexOf(new Pair<Integer, Path>(queryId, oldPath)), 
							new Pair<Integer, Path>(queryId, newPath));	
					
					System.out.println("Decided to replace queryId = " + queryId + 
							" with an empty path.");
					
					break; 
				}															
				
				Iterator<Path> heapIterator = heap.iterator();
				
				//heap is non-empty hence iterator hasNext()
				
				Path newPath = heapIterator.next();
				Path oldPath = new2old.get(newPath);
				
				int queryId = switchPaths(oldPath, newPath, startTime, path2queries);
				result.set(result.indexOf(new Pair<Integer, Path>(queryId, oldPath)), 
						new Pair<Integer, Path>(queryId, newPath));
				
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
			paths.add(queriesWithSolutions.get(i).second());
			System.out.println("QueryId = " + queriesWithSolutions.get(i).first() +
					" {" + queryHandler.getQuery(i).first() + ", " + queryHandler.getQuery(i).second() + "}" +
					" Solution = " + queriesWithSolutions.get(i).second());
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(dbr.evaluate(paths));
		dbr.showLoad();
		
		System.out.println(dbr.listOfPaths);

	}

}
