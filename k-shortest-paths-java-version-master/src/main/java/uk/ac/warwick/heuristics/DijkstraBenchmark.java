package uk.ac.warwick.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.warwick.queries.Query;
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
			int startTime = query.getStartTime();
			
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
				updateLoad(benchmarkPath, startTime, false);
			}
			result.add(new Pair<Query, Path> (query, benchmarkPath));
		}
		
		return result;
	}
	

	public List<Pair<Query, Path>> process(Collection<Query> queries,
			boolean capacityAware) {
		return process(queries); 
	}
}
