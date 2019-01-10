package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import uk.ac.warwick.settings.Settings;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.shortestpaths.DijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;


/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * This class is first heuristic to solve collective shortest path optimization problem.
 * It simply assign a shortest path w.r.t to the given traffic load 
 * (caused by previous queries solutions) if it is possible.
 * If the algorithm does not find any feasible solution it assigns empty path of infinite weight.
 * 
 * This one allows only waiting in the first (starting) node.
 */
public class SequentialDijkstraOld extends AbstractSolution{
	static int debug = 0; 																			

	public SequentialDijkstraOld(MyVariableGraph graph) {														
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, Map<Path, Integer>>();									// I'd prefer TreeMap but it doesn't work because of vertex.compareTo
																									// that is used in Dijkstra (i.e., compares weights) not ids -- fixed
																									// Map<Path, Integer> is a replacement for multiset data structure
		dijkstra = new DijkstraShortestPathAlg(graph);
	}
	
	public SequentialDijkstraOld(MyVariableGraph graph, Map<Edge, int[]> load) {									
		this(graph);																				// calling the other constructor
		this.load = load;
	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries, boolean capacityAware) {
		
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query : queries) {
			int startTime = query.getStartTime();
			Path path = processQuery(query, capacityAware);
			if (path.size() > 0){
				if (Settings.DEBUG_LEVEL >= 1)
					System.out.println("Algorithm found a path from "
							+ query.first() + " to " + query.second() + " at time " + 
							query.getStartTime() + " (" + query.getInitialStartTime() + ") " + debug);
				debug++;
				updateLoad(path, startTime, false);													// it automatically updates dijkstra.load
			}
			else {
				if (Settings.DEBUG_LEVEL >= 1)
				System.out.println("Algorithm failed to find a path from " 
						+ query.first() + " to " + query.second() + " at time " + 
						query.getStartTime() + " (" + query.getInitialStartTime() + ") " + debug);
				
			}
			result.add(new Pair<Query, Path> (query, path));
		}
		return result;																				// our result variable contains failed queries
	}
	
	
	public int getMaxWaitingTime(List<Path> paths) {												// waiting time in the starting node (useless in this case)
		int maximumWaitingTime = 0;
		
		for (Path path : paths) {
			if (maximumWaitingTime < path.getWaitingTime())
				maximumWaitingTime = path.getWaitingTime();
		}
		return maximumWaitingTime;
	}
	
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		MyVariableGraph graph = new MyVariableGraph(graphPath);
		SequentialDijkstra seqDijkstra = new SequentialDijkstra(graph); 							
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), capacityAware);
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
		System.out.println(seqDijkstra.evaluate(paths));
		System.out.print("maximumWaitingTime = ");
		System.out.println(seqDijkstra.getMaxWaitingTime(paths));
		seqDijkstra.showLoad();
		
		System.out.println(seqDijkstra.listOfPaths);

		
	}

}
