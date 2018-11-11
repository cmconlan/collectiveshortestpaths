package uk.ac.warwick.thomas.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import uk.ac.warwick.heuristics.SequentialDijkstra;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Pair;

/**
 * 
 * @author Tomasz Janus
 *
 */

public class SequentialDijkstraTest {
	private Graph graph;
	private SequentialDijkstra seqDijkstra;
	private QueryHandler queryHandler;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeTest
	public void setUp() throws Exception {
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		// Import the graph from a file
		graph = new Graph(graphPath);
		seqDijkstra = new SequentialDijkstra(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
	}

	@Test
	public void seqDijkstraTest()	{
		System.out.println("\n\n##Sequential Dijkstra Heuristic Test");
		int expectedNumberOfFailures = 1;
		int expectedTravelTimeOfTheRest = 4;
		boolean capacityAware = true;
		List<Pair<Integer, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), capacityAware);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			int queryId = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			System.out.println("QueryId = " + queriesWithSolutions.get(i).first() +
					" {" + queryHandler.getQuery(queryId).first() + ", " + queryHandler.getQuery(queryId).second() + "}" +
					" Solution = " + solution);
		}
//		
		assert seqDijkstra.evaluate(paths).first() == expectedNumberOfFailures;
		assert seqDijkstra.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		seqDijkstra.showLoad();	
	}
}
