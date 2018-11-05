package uk.ac.warwick.heuristics;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
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
	
	
	public List<Pair<Query, Path>> process(List<Query> queries, int startTime) {
		boolean capacityAware = false;
		List<Pair<Query, Path>> result = super.process(queries, startTime, capacityAware);			// at first we want to calculate all the shortest paths
																									// ignoring capacities
		
		// we need to identify edgeTime violating our capacity constraints:
		
		
		for (Edge edge : load.keySet()) {
			for (int t = startTime; t < load.get(edge).length; ++t) {
				if (load.get(edge)[t] > graph.getEdgeCapacity(edge.first(), edge.second())) {
					EdgeTime edgeTime = new EdgeTime(edge, t);
//					System.out.println("--HERE--");
//					System.out.println(edgeTime);
//					System.out.println(listOfPaths.get(edgeTime));
//					System.out.println("--END OF HERE--");
//					listOfPaths.get(edgeTime);														// here are the Paths violating (edge, t) constraints
					problematicEdges.add(edgeTime);
				}
			}
		}
		
		for (EdgeTime edgeTime : problematicEdges) {
			while (load.get(edgeTime.first())[edgeTime.second()] 
					> graph.getEdgeCapacity(edgeTime.first().first(), edgeTime.first().second())) {
				// create Heap h
				Queue<Path> queue = new PriorityQueue<Path>();
				
				for (Path oldPath : listOfPaths.get(edgeTime)) {
					updateLoad(oldPath, startTime, true);											// we don't want to consider path p in our trafficLoad
					Query query = new Query(oldPath.getFirst(), oldPath.getLast());
					Path newPath = super.processQuery(query, startTime, true);						
					
					if (newPath.size() > 0){
						queue.add(newPath);
					}
					// we need to restore previous state of the world 
					updateLoad(oldPath, startTime, false);											// [TODO] do not modify listOfPaths
					
				}
			}
		}
		
		
		// our result 
		return result;
	}

	public static void main(String[] args) {
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		Graph graph = new Graph(graphPath);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		
		int startTime = 0;
		DijkstraBasedReplacement dbr = new DijkstraBasedReplacement(graph);
		System.out.println(dbr.process(queryHandler.getQueries(), startTime));
		dbr.showLoad();
	}

}
