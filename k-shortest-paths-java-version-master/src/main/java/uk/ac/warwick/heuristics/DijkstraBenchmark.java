package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.shortestpaths.ModifiedDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;

public class DijkstraBenchmark extends AbstractSolution {
//	protected MyVariableGraph graph;
//	protected Map<Edge, int[]> load;																// for each (edge, time) we keep traffic load
//	protected BaseDijkstraShortestPathAlg dijkstra;
//	
//	protected Map<EdgeTime, Map<Path, Integer>> listOfPaths;										// Map<Path, Integer> is a replacement for multiset data structure
	
	public DijkstraBenchmark(MyVariableGraph graph) {														
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, Map<Path, Integer>>();
		dijkstra = new ModifiedDijkstraShortestPathAlg(graph);
	}
	
	public DijkstraBenchmark(MyVariableGraph graph, Map<Edge, int[]> load) {									
		this(graph);																				// calling the other constructor
		this.load = load;
	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries) {
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query: queries) {
			graph.useWholeGraph(true);
			Path shortestPath = processQuery(query, false);
//			System.out.println("SP::");
//			System.out.println(shortestPath);
			
			graph.clearAvailableEdges();
			for (int i = 0; i < shortestPath.size() - 1; ++i) {
				Edge edge = new Edge(shortestPath.get(i), shortestPath.get(i + 1));
				graph.addAvailableEdge(edge);
			}
			graph.useWholeGraph(false);
			Path benchmarkPath = processQuery(query, true);
//			System.out.println("BP::");
//			System.out.println(benchmarkPath);
			
			if (benchmarkPath.size() > 0) {
				updateLoad(benchmarkPath, false);
			}
			result.add(new Pair<Query, Path> (query, benchmarkPath));
		}
		
		return result;
	}
	

	public List<Pair<Query, Path>> process(Collection<Query> queries,
			boolean capacityAware) {
		return process(queries); 
	}
	
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/Chris_graph_fixed.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		MyVariableGraph graph = new MyVariableGraph(graphPath);
		DijkstraBenchmark db = new DijkstraBenchmark(graph);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		System.out.println("tutaj");
		//[179, 180, 150, 120, 90, 11, 15, 17, 13, 18, 21, 486, 466, 446, 447, 448, 449]
		int s = 0;
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(179), graph.getVertex(180)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(180), graph.getVertex(150)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(150), graph.getVertex(120)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(120), graph.getVertex(90)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(90),  graph.getVertex(11)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(11),  graph.getVertex(15)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(15),  graph.getVertex(17)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(17),  graph.getVertex(13)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(13),  graph.getVertex(18)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(18),  graph.getVertex(21)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(21),  graph.getVertex(486)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(486), graph.getVertex(466)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(466), graph.getVertex(446)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(446), graph.getVertex(447)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(447), graph.getVertex(448)));
		System.out.println(s += graph.getEdgeWeight(graph.getVertex(448), graph.getVertex(449)));
		System.out.println("koniec");
		
		List<Pair<Query, Path>> benchmarkQueriesWithSolutions = db.process(queryHandler.getQueries());
		

		
		List<Path> benchmarkPaths = new ArrayList<Path>();
		for (int i = 0; i < benchmarkQueriesWithSolutions.size(); ++i) {
			Path solution = benchmarkQueriesWithSolutions.get(i).second();
			benchmarkPaths.add(solution);
			if (i < 10) {
				System.out.println("QueryId = " + benchmarkQueriesWithSolutions.get(i).first().getId() +
						" {" + benchmarkQueriesWithSolutions.get(i).first().first() + ", " + benchmarkQueriesWithSolutions.get(i).first().second() + "}" +
						" Solution = " + solution);
				System.out.println(solution.getArrivalTimes());
			}
		}
		
		System.out.println("##Benchmark::");
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(db.evaluate(benchmarkPaths));
//		System.out.print("maximumWaitingTime = ");
//		System.out.println(greedySeqDijkstra.getMaxWaitingTime(benchmarkPaths));


		
	}
}
