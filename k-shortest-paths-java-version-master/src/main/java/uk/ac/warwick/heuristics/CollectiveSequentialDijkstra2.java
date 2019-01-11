package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.Pair;
/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 * 
 * This approach outperforms CollectiveSequentialDijkstra.
 * What matters is number of edges a car is saturating not its total travel time.
 *
 */
public class CollectiveSequentialDijkstra2 extends SequentialDijkstra {
	
	Comparator<Query> queryComparator = new Comparator<Query>() {
        public int compare(Query q1, Query q2) {
        	if(q1.getExpectedNumerOfNodesOnTheWay() != q2.getExpectedNumerOfNodesOnTheWay()) 
        		return q1.getExpectedNumerOfNodesOnTheWay() - q2.getExpectedNumerOfNodesOnTheWay();
        	else return q1.getId() - q2.getId();
        }
    };
	
	public CollectiveSequentialDijkstra2(MyVariableGraph graph) {
		super(graph);
	}
	
	public CollectiveSequentialDijkstra2(MyVariableGraph graph, Map<Edge, int[]> load) {
		super(graph, load);
	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries, boolean capacityAware) {
		Set<Query> sortedQueries = new TreeSet<Query>(queryComparator);
		
		List<Pair<Query, Path>> firstRun = super.process(queries, false); // we assign shortest paths to the queries;
		
		for (Pair<Query, Path> pair : firstRun) {
			Query query = pair.first();
			Path path = pair.second();
			query.setExpectedNumerOfNodesOnTheWay(path.size());
			query.setExpectedTravelTime(path.getWeight());
			sortedQueries.add(query);
		}
		
		return super.process(sortedQueries, capacityAware);
	}
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/Chris_graph_fixed.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		MyVariableGraph graph = new MyVariableGraph(graphPath);
		BaseSolution seqDijkstra = new CollectiveSequentialDijkstra2(graph); 							
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), capacityAware);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
//			Query query = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			if (i < 10) {
				System.out.println("QueryId = " + queriesWithSolutions.get(i).first().getId() +
						" {" + queriesWithSolutions.get(i).first().first() + ", " + queriesWithSolutions.get(i).first().second() + "}" +
						" Solution = " + solution);
			}
		}
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
//		System.out.print("maximumWaitingTime = ");
//		System.out.println(seqDijkstra.getMaxWaitingTime(paths));
//		seqDijkstra.showLoad();
		
//		System.out.println(seqDijkstra.listOfPaths);

		
	}
}	
