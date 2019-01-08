package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import uk.ac.warwick.settings.Settings;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.Pair;

/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * This class performs greedy solution assignment as long as it is possible.
 * When it is not possible it looks for a solution in the future, i.e., moves query in time.
 */

public class GreedySequentialDijkstra extends SequentialDijkstra{

	public GreedySequentialDijkstra(MyVariableGraph graph) {
		super(graph);
	}
	
	public GreedySequentialDijkstra(MyVariableGraph graph, Map<Edge, int[]> load) {
		super(graph, load);
	}

	
	public List<Pair<Query, Path>> process(Collection<Query> queries, boolean capacityAware) {
		Set<Query> unresolvedQueries = new TreeSet<Query>(queries);									// copy
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query,Path>>();
		
		while (!unresolvedQueries.isEmpty()) {
			List<Pair<Query, Path>> processResult = super.process(unresolvedQueries, capacityAware);
			unresolvedQueries.clear();																
			
			for (Pair<Query, Path> pair : processResult) {
				if (pair.second().getWeight() == Graph.DISCONNECTED) {
					Query oldQuery = pair.first();
					Query newQuery = new Query(oldQuery.first(), oldQuery.second(), oldQuery.getStartTime() + 1);
					newQuery.setInitialStartTime(oldQuery.getInitialStartTime());					// we need to remember initial query startTime
					unresolvedQueries.add(newQuery);
				}
				else {
					Query query = pair.first();
					Path path = pair.second();
					int waitingTime = query.getStartTime()- query.getInitialStartTime();
					path.setWeight(path.getWeight() + waitingTime);
					path.setWaitingTime(waitingTime);
					if (Settings.DEBUG_LEVEL >= 1) {
						System.out.println(query + " waiting time = " + waitingTime);
					}
					result.add(pair);
				}
			}
			
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		MyVariableGraph graph = new MyVariableGraph(graphPath);
		GreedySequentialDijkstra greedySeqDijkstra = new GreedySequentialDijkstra(graph); 							
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = greedySeqDijkstra.process(queryHandler.getQueries(), capacityAware);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			Query query = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			System.out.println("QueryId = " + query.getId() +
					" {" + query.first() + ", " + query.second() + "}" +
					" Solution = " + solution);
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(greedySeqDijkstra.evaluate(paths));
		System.out.print("maximumWaitingTime = ");
		System.out.println(greedySeqDijkstra.getMaxWaitingTime(paths));
		greedySeqDijkstra.showLoad();
		
		System.out.println(greedySeqDijkstra.listOfPaths);

		
	}
}
