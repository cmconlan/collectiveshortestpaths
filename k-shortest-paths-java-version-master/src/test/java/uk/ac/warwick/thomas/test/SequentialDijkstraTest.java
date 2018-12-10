package uk.ac.warwick.thomas.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import uk.ac.warwick.heuristics.SequentialDijkstra;
import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;
import edu.asu.emit.algorithm.utils.Pair;

/**
 * 
 * @author Tomasz Janus
 *
 */

public class SequentialDijkstraTest {
	private BaseGraph graph;
	private SequentialDijkstra seqDijkstra;
	private QueryHandler queryHandler;
	
	/**
	 * @throws java.lang.Exception
	 */
	//@BeforeTest
	public void setUp() throws Exception {
		//Common preprocessing
	}

	//@Test
	public void seqDijkstraTest()	{
		String graphPath = "data/graphs/graph1.txt";
		String queriesPath = "data/queries/queries1.txt";
		
		// Import the graph from a file
		graph = new Graph(graphPath);
		seqDijkstra = new SequentialDijkstra(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
		
		System.out.println("\n\n##Sequential Dijkstra Heuristic Test");
		int expectedNumberOfFailures = 1;
		int expectedTravelTimeOfTheRest = 4;
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
//		
		assert seqDijkstra.evaluate(paths).first() == expectedNumberOfFailures;
		assert seqDijkstra.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		seqDijkstra.showLoad();	
	}
	
//	@Test
	public void ElifsData() {
		System.out.println("\n\n##Sequential Dijkstra Heuristic Test -- Elif's data");
		
		String graphPath = "data/graphs/Elif.txt";
		String queriesPath = "data/queries/Elif200.txt";
		
		graph = new Graph(graphPath);
		seqDijkstra = new SequentialDijkstra(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
//		int expectedNumberOfFailures = 5;															
//		int expectedTravelTimeOfTheRest = 65858;													// old result using map {5, 65858}
//		int expectedTravelTimeOfTheRest = 66004;													
																									// expected result using set (without shifting) {5, 66004}
																									// expected result withShift {0, 67567}
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), capacityAware);
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
//		assert seqDijkstra.evaluate(paths).first() == expectedNumberOfFailures;
//		assert seqDijkstra.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		System.out.print("maximumWaitingTime = ");
		System.out.println(seqDijkstra.getMaxWaitingTime(paths));
//		seqDijkstra.showLoad();	
	
	}
	
// #######################################3
	
	//@Test
	public void ChrisData() {																		// result: {nFailed, totalTravelTime} = Pair{12970, 115282} [total 14648]
		System.out.println("\n\n##Sequential Dijkstra Heuristic Test -- Chris's data");
		
		String graphPath = "data/graphs/Chris_graph_fixed.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		graph = new Graph(graphPath);
		seqDijkstra = new SequentialDijkstra(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
//		int expectedNumberOfFailures = 5;															
//		int expectedTravelTimeOfTheRest = 65858;													// old result using map {5, 65858}
//		int expectedTravelTimeOfTheRest = 66004;													
																									// expected result using set (without shifting) {5, 66004}
																									// expected result withShift {0, 67567}
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = seqDijkstra.process(queryHandler.getQueries(), capacityAware);
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
//		assert seqDijkstra.evaluate(paths).first() == expectedNumberOfFailures;
//		assert seqDijkstra.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(seqDijkstra.evaluate(paths));
		System.out.print("maximumWaitingTime = ");
		System.out.println(seqDijkstra.getMaxWaitingTime(paths));
//		seqDijkstra.showLoad();	
	
	}

}
