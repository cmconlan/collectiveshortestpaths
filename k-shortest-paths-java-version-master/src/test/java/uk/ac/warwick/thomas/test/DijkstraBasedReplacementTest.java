package uk.ac.warwick.thomas.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import uk.ac.warwick.heuristics.DijkstraBasedReplacement;
import uk.ac.warwick.queries.Query;
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
	//@BeforeTest
	public void setUp() throws Exception {
		//Common preprocessing
	}

	@Test
	public void dijkstraBasedReplacementTest()	{
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		// Import the graph from a file
		graph = new Graph(graphPath);
		dbr = new DijkstraBasedReplacement(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
		System.out.println("\n\n##Dijkstra Based Replacement Heuristic Test");
		int expectedNumberOfFailures = 1;
		int expectedTravelTimeOfTheRest = 4;
		int startTime = 0;
		List<Pair<Query, Path>> queriesWithSolutions = dbr.process(queryHandler.getQueries(), startTime);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
			Query query = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
			System.out.println("QueryId = " + query.getId() +
					" {" + query.first() + ", " + query.second() + "}" +
					" Solution = " + solution);
		}
		
		assert dbr.evaluate(paths).first() == expectedNumberOfFailures;
		assert dbr.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(dbr.evaluate(paths));
		dbr.showLoad();
	}
	
	@Test
	public void ElifsData() {
		System.out.println("\n\n##Dijkstra Based Replacement Heuristic Test -- Elif's data");
		
		String graphPath = "data/graphs/Elif.txt";
		String queriesPath = "data/queries/Elif200.txt";
		
		graph = new Graph(graphPath);
		dbr = new DijkstraBasedReplacement(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
		int expectedNumberOfFailures = 5;
//		int expectedTravelTimeOfTheRest = 66013;													// heap picks the shortest path
		int expectedTravelTimeOfTheRest = 66004;													// heap picks relative shortest path w.r.t. the true shortest path
		
		int startTime = 0;
		List<Pair<Query, Path>> queriesWithSolutions = dbr.process(queryHandler.getQueries(), startTime);
		List<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < queriesWithSolutions.size(); ++i) {
//			Query query = queriesWithSolutions.get(i).first();
			Path solution = queriesWithSolutions.get(i).second();
			paths.add(solution);
//			System.out.println("QueryId = " + query.getId() +
//					" {" + query.first() + ", " + query.second() + "}" +
//					" Solution = " + solution);
		}
//		
		assert dbr.evaluate(paths).first() == expectedNumberOfFailures;
		assert dbr.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(dbr.evaluate(paths));
//		seqDijkstra.showLoad();	
	
	}
}
