package uk.ac.warwick.thomas.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import uk.ac.warwick.heuristics.DijkstraBasedReplacement;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Pair;

public class DijkstraBasedReplacementTest {
	private Graph graph;
	private DijkstraBasedReplacement dbr;
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
		dbr = new DijkstraBasedReplacement(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
	}

	@Test
	public void dijkstraBasedReplacementTest()	{
		System.out.println("\n\n##Dijkstra Based Replacement Heuristic Test");
		int expectedNumberOfFailures = 1;
		int expectedTravelTimeOfTheRest = 4;
		int startTime = 0;
		List<Pair<Integer, Path>> queriesWithSolutions = dbr.process(queryHandler.getQueries(), startTime);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			int queryId = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			System.out.println("QueryId = " + queryId +
					" {" + queryHandler.getQuery(queryId).first() + ", " + queryHandler.getQuery(queryId).second() + "}" +
					" Solution = " + solution);
		}
		
		assert dbr.evaluate(paths).first() == expectedNumberOfFailures;
		assert dbr.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(dbr.evaluate(paths));
		dbr.showLoad();
	}
}
