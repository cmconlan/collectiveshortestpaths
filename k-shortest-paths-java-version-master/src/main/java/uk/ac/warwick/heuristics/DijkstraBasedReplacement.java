package uk.ac.warwick.heuristics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.utils.Pair;

public class DijkstraBasedReplacement extends SequentialDijkstra {
	
//	temporary - just not to switch file all the time
	
//	protected Graph graph;
//	protected List<Query> queries;
//	protected Map<Edge, int[]> load;
	
	
	public DijkstraBasedReplacement(String graphPath, String queriesPath) {
		super(graphPath, queriesPath);
	}
	
	
	public List<Pair<Pair<BaseVertex, BaseVertex>, Path>> process() {
		boolean capacityAware = false;
		List<Pair<Pair<BaseVertex, BaseVertex>, Path>> result = super.process(capacityAware);		// at first we want to calculate all the shortest paths
																									// ignoring capacities
		
		int startTime = 0;																			// this is temporarySolution since we assume all the traffic starts at time 0
		// we need to identify edgeTime violating our capacity constraints:
		
		for (Pair<BaseVertex, BaseVertex> edge : load.keySet()) {
			for (int t = startTime; t < load.get(edge).length; ++t) {
				if (load.get(edge)[t] > graph.getEdgeCapacity(edge.first(), edge.second())) {
					
				}
			}
		}
		
		
		// our result 
		return result;
	}

	public static void main(String[] args) {
		DijkstraBasedReplacement dbr = new DijkstraBasedReplacement("data/graphs/graph1.txt", "data/queries/queries1.txt");
		System.out.println(dbr.process());
		dbr.showLoad();
	}

}
