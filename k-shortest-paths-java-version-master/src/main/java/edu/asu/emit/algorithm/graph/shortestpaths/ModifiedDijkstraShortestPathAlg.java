package edu.asu.emit.algorithm.graph.shortestpaths;

import java.util.Comparator;
import java.util.PriorityQueue;

import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.utils.Pair;

public class ModifiedDijkstraShortestPathAlg extends DijkstraShortestPathAlg {
	
	// Custom comparator to PriorityQueue
		Comparator<Pair<BaseVertex, Integer>> weightComparator = new Comparator<Pair<BaseVertex, Integer>>() {
	        public int compare(Pair<BaseVertex, Integer> v1, Pair<BaseVertex, Integer> v2) {
	            return v1.first().getWeight() - v2.first().getWeight();
	        }
	    };
	    
	private PriorityQueue<Pair<BaseVertex, Integer>> vertexCandidateQueue = 
			new PriorityQueue<Pair<BaseVertex, Integer>>(weightComparator);
	
	public ModifiedDijkstraShortestPathAlg(BaseGraph graph) {
		super(graph);
	}

	/**
	 * Update the distance from the source to the concerned vertex.
	 * @param vertex
	 */
	private void updateVertex(BaseVertex vertex, boolean isSource2sink)	{
		/**
		 * [TODO] it must contain a map: edge -> boolean
		 * and put vertex as a determined only if all the edges are used
		 * otherwise it should add vertex which is currently in use to the queue 
		 * (with consecutive timeStep)
		 */
	}
}
