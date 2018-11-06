package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
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
		
		Map<Path, Integer> path2query = new HashMap<Path, Integer>();								// we need to be able to figure out which query solves our resulting path					
		for (Pair<Integer, Path> pair : result) {
			path2query.put(pair.second(), pair.first());
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
			System.out.println("EdgeTime: " + edgeTime);
			while (load.get(edgeTime.first())[edgeTime.second()] 									
					> graph.getEdgeCapacity(edgeTime.first().first(), edgeTime.first().second())) {
				System.out.println("EdgeTimeLoad: " + load.get(edgeTime.first())[edgeTime.second()]  + 
						"/" + graph.getEdgeCapacity(edgeTime.first().first(), edgeTime.first().second()));
				// create Heap h
				Set<Path> heap = new TreeSet<Path>();
				List<Path> pathsToReplace = new ArrayList<Path>();
				Map<Path, Path> new2old = new HashMap<Path, Path>();
				for (Path path : listOfPaths.get(edgeTime)) {
					 pathsToReplace.add(path);
				}
				
				for (Path oldPath : pathsToReplace) {
					updateLoad(oldPath, startTime, true);
					int queryId = path2query.get(oldPath);
					Query query = queries.get(queryId); 
					
					Path newPath = super.processQuery(query, startTime, true);
					if (newPath.size() > 0) {
						heap.add(newPath);
						new2old.put(newPath, oldPath);
					}
					
					updateLoad(oldPath, startTime, false);											// restoring former state of the world
				}
				
				if (heap.isEmpty()) break;															// we haven't found any good candidate
				
				Iterator<Path> iterator = heap.iterator();
				
				//heap was non-empty hence iterator hasNext()
				
				Path newPath = iterator.next();
				Path oldPath = new2old.get(newPath);
				
				//update state of the world (i.e., replace those paths in our traffic load)
				updateLoad(oldPath, startTime, true);
				updateLoad(newPath, startTime, false);
				
				int queryId = path2query.get(oldPath);
																									
//				Query query = queries.get(queryId);
//				System.out.println("HERE");
//				System.out.println(result.contains(new Pair<Query, Path> (query, oldPath)));
//				System.out.println(new Pair<Integer, Path>(queryId, oldPath));
//				System.out.println(new Pair<Integer, Path>(queryId, newPath));
//				System.out.println(result.indexOf(new Pair<Integer, Path>(queryId, oldPath)));
//				System.out.println(result);
				
				result.set(result.indexOf(new Pair<Integer, Path>(queryId, oldPath)), new Pair<Integer, Path>(queryId, newPath));
				
//				System.out.println(result);
				
				//erase oldPath from listOfPaths
				updateListOfPaths(edgeTime, oldPath, true);
				
			}
		}
		
		
		// our result 
		return result;
	}

	public static void main(String[] args) {
		String graphPath = "data/graphs/graph2.txt";
		String queriesPath = "data/queries/queries2.txt";
		
		Graph graph = new Graph(graphPath);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		
		int startTime = 0;
		DijkstraBasedReplacement dbr = new DijkstraBasedReplacement(graph);
		System.out.println(dbr.process(queryHandler.getQueries(), startTime));
		dbr.showLoad();
	}

}
