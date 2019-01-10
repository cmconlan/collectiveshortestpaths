package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.warwick.queries.Query;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.Pair;

/**
 * 
 * @author jano
 * @email t.janus@warwick.ac.uk
 * 
 * This one allows only waiting in the first (starting) node.
 */

public class SequentialDijkstraWithThresholdOld extends SequentialDijkstraOld{

	private double threshold;
	
	public SequentialDijkstraWithThresholdOld(MyVariableGraph graph, double threshold) {
		super(graph);
		this.setThreshold(threshold);
	}
	
	public SequentialDijkstraWithThresholdOld(MyVariableGraph graph, double threshold, Map<Edge, int[]> load) {
		super(graph, load);
		this.setThreshold(threshold);
	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries, boolean capacityAware) {
		Set<Query> unresolvedQueries = new TreeSet<Query>(queries);									// copy
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		while (!unresolvedQueries.isEmpty()) {
			System.out.println("unresolvedQueries.size() = " + unresolvedQueries.size());
			Set<Query> newUnresolvedQueries = new TreeSet<Query>();									
			for (Query query : unresolvedQueries) {
				int startTime = query.getStartTime();
				Path shortestPath = processQuery(query, false);
				Path candidatePath = processQuery(query, true);
				
				double delta = candidatePath.getWeight() - shortestPath.getWeight();
				delta /= candidatePath.getWeight();
				candidatePath.setDelta(delta);
				
				if (candidatePath.getDelta() < threshold) {											// we do not consider waiting time here
					result.add(new Pair<Query, Path> (query, candidatePath));
					updateLoad(candidatePath, startTime, false);
				}
				else {
					Query newQuery = new Query(query.first(), query.second(), startTime + 1);
					newQuery.setInitialStartTime(query.getInitialStartTime());
					newUnresolvedQueries.add(newQuery);
				}
			}
			unresolvedQueries = newUnresolvedQueries;
		}
		
		for (Pair<Query, Path> pair : result) {														// but we want to add waiting time to evaluate our solution
			Path path = pair.second();
			Query query = pair.first();
			int waitingTime = query.getStartTime() - query.getInitialStartTime();
			path.setWeight(path.getWeight() + waitingTime);
			path.setWaitingTime(waitingTime);
		}
		
		return result;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
