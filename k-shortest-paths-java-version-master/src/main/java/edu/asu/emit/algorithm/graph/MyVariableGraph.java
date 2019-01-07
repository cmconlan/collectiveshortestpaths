package edu.asu.emit.algorithm.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.warwick.heuristics.SequentialDijkstra;
import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.utils.Edge;
import edu.asu.emit.algorithm.utils.Pair;

public class MyVariableGraph extends Graph {
	private Set<Edge> availableEdgeSet = new HashSet<Edge>();
	private boolean useWholeGraph = true;
	
	public MyVariableGraph(final String dataFileName) {
		super(dataFileName);
	}
	
	public MyVariableGraph(Graph graph) {
		super(graph);
	}
	
	public void clearAvailableEdges() {
		availableEdgeSet.clear();
	}
	
	public void addAvailableEdge(Edge edge) {
		availableEdgeSet.add(edge);
	}
	
	public Set<BaseVertex> getAdjacentVertices(BaseVertex vertex) {
		if (useWholeGraph) return super.getAdjacentVertices(vertex);
		else {
			Set<BaseVertex> result = new HashSet<BaseVertex>();
			for (BaseVertex neighbor : super.getAdjacentVertices(vertex)) {
				Edge edge = new Edge(vertex, neighbor);
				if (availableEdgeSet.contains(edge)) {
					result.add(neighbor);
				}
			}
			return result;
		}
	}
	
	public Set<BaseVertex> getPrecedentVertices(BaseVertex vertex) {
		if (useWholeGraph) return super.getPrecedentVertices(vertex);
		else {
			Set<BaseVertex> result = new HashSet<BaseVertex>();
			for (BaseVertex ancestor : super.getPrecedentVertices(vertex)) {
				Edge edge = new Edge(ancestor, vertex);
				if (availableEdgeSet.contains(edge)) {
					result.add(ancestor);
				}
			}
			return result;
		}
	}
	
	public void useWholeGraph(boolean value) {
		useWholeGraph = value;
	}
	
	public static void main(String[] args) {
		System.out.println("\n\n##MyVariableGraphTest -- Chris's data");
		
		String graphPath = "data/graphs/Chris_graph_fixed.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		Graph graph = new Graph(graphPath);
		MyVariableGraph myVariableGraph = new MyVariableGraph(graph);
		SequentialDijkstra seqDijkstra = new SequentialDijkstra(myVariableGraph);
		QueryHandler queryHandler = new QueryHandler(graph, queriesPath);
		
		Set<Query> smallQueries = new TreeSet<Query>();
		
		int k = 0;
		for (Query query : queryHandler.getQueries()) {
			smallQueries.add(query);
			k++;
			if (k == 10) break;
		}
		
		boolean capacityAware = false;
		List<Pair<Query, Path>> results = seqDijkstra.process(smallQueries, capacityAware);
		
		boolean isFirst = true;
		
		for (Pair<Query, Path> item : results) {
			System.out.println(item.first() + " " + item.second());
			if (isFirst) {
				isFirst = false;
				List<BaseVertex> myPath = item.second().getVertexList();
				for (int i = 0; i < myPath.size() - 1; ++i) {
					Edge edge = new Edge(myPath.get(i), myPath.get(i + 1));
					myVariableGraph.addAvailableEdge(edge);
				}
			}
		}
		
		System.out.println("------------");
		
		myVariableGraph.useWholeGraph(false);
		
		results = seqDijkstra.process(smallQueries, capacityAware);
		
		for (Pair<Query, Path> item : results) {
			System.out.println(item.first() + " " + item.second());
		}
	
	}
}
