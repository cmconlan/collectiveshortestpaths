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
import edu.asu.emit.algorithm.graph.VariableGraph;
import edu.asu.emit.algorithm.graph.shortestpaths.ModifiedDijkstraShortestPathAlg;
import edu.asu.emit.algorithm.graph.shortestpaths.YenTopKShortestPathsAlg;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.EdgeTime;
import edu.asu.emit.algorithm.utils.Pair;

public class TopKBenchmark extends AbstractSolution {
//	protected MyVariableGraph graph;
//	protected Map<Edge, int[]> load;																// for each (edge, time) we keep traffic load
//	protected BaseDijkstraShortestPathAlg dijkstra;
//	
//	protected Map<EdgeTime, Map<Path, Integer>> listOfPaths;										// Map<Path, Integer> is a replacement for multiset data structure
	
	public YenTopKShortestPathsAlg topK;
	private VariableGraph vGraph;
	private int k;
	
	public TopKBenchmark(MyVariableGraph graph) {														
		this(graph, 3);
	}
	
	public TopKBenchmark(MyVariableGraph graph, int k) {														
		this.graph = graph;
		load = new HashMap<Edge, int[]>();
		listOfPaths = new TreeMap<EdgeTime, Map<Path, Integer>>();
		dijkstra = new ModifiedDijkstraShortestPathAlg(graph);
		vGraph = new VariableGraph(graph);
		topK = new YenTopKShortestPathsAlg(vGraph);
		this.k = k;
	}
	
	public TopKBenchmark(MyVariableGraph graph, Map<Edge, int[]> load) {									
		this(graph);																				// calling the other constructor
		this.load = load;
	}
	
	public List<Pair<Query, Path>> process(Collection<Query> queries) {
		List<Pair<Query, Path>> result = new ArrayList<Pair<Query, Path>>();
		
		for (Query query: queries) {
			int startTime = query.getStartTime();
			
			List<Path> shortestPaths = processQueryTopK(query);
//			System.out.println("SP::");
//			System.out.println(shortestPath);
			
			graph.clearAvailableEdges();
			for (int j = 0; j < shortestPaths.size(); ++j) {
				for (int i = 0; i < shortestPaths.get(j).size() - 1; ++i) {
					Edge edge = new Edge(shortestPaths.get(j).get(i), shortestPaths.get(j).get(i + 1)); // i. and (i+1). vertices of j. shortest path
					graph.addAvailableEdge(edge);
				}
			}
			graph.useWholeGraph(false);
			Path benchmarkPath = processQuery(query, true);
//			System.out.println("BP::");
//			System.out.println(benchmarkPath);
			
			if (benchmarkPath.size() > 0) {
				updateLoad(benchmarkPath, startTime, false);
			}
			result.add(new Pair<Query, Path> (query, benchmarkPath));
		}
		
		return result;
	}
	
	static int debug = 0;
	
	public List<Path> processQueryTopK(Query query) {
	System.out.println(query.first() + " " + query.second());
	System.out.println("Query.id == " + query.getId());
	System.out.println("Query.nr == " + debug++);
		return topK.getShortestPaths(query.first(), query.second(), k);
	}
	
	

	public List<Pair<Query, Path>> process(Collection<Query> queries,
			boolean capacityAware) {
		return process(queries); 
	}
	
	
	public static void main(String[] args) {
		
		String graphPath = "data/graphs/Chris_graph_fixed.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		MyVariableGraph graph = new MyVariableGraph(graphPath);
		TopKBenchmark topKbenchmark = new TopKBenchmark(graph);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		System.out.println(topKbenchmark.topK.getShortestPaths(graph.getVertex(460), graph.getVertex(296), 1));
		
//		List<Pair<Query, Path>> benchmarkQueriesWithSolutions = topKbenchmark.process(queryHandler.getQueries());
//		
//		List<Path> benchmarkPaths = new ArrayList<Path>();
//		for (int i = 0; i < benchmarkQueriesWithSolutions.size(); ++i) {
//			Path solution = benchmarkQueriesWithSolutions.get(i).second();
//			benchmarkPaths.add(solution);
//			if (i < 10) {
//				System.out.println("QueryId = " + benchmarkQueriesWithSolutions.get(i).first().getId() +
//						" {" + benchmarkQueriesWithSolutions.get(i).first().first() + ", " + benchmarkQueriesWithSolutions.get(i).first().second() + "}" +
//						" Solution = " + solution);
//			}
//		}
//		
//		System.out.println("##Benchmark::");
//		System.out.print("{nFailed, totalTravelTime} = ");
//		System.out.println(topKbenchmark.evaluate(benchmarkPaths));
//		System.out.print("maximumWaitingTime = ");
//		System.out.println(greedySeqDijkstra.getMaxWaitingTime(benchmarkPaths));


		
	}
}
