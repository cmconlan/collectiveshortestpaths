package uk.ac.warwick.thomas.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import uk.ac.warwick.heuristics.BaseSolution;
import uk.ac.warwick.heuristics.DijkstraBenchmark;
import uk.ac.warwick.heuristics.GreedySequentialDijkstra;
import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.MyVariableGraph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Pair;

public class GreedySequentialDijkstraTest {
	private MyVariableGraph graph;
	private BaseSolution greedySeqDijkstra;
	private QueryHandler queryHandler;
	
//	@Test
	public void ElifsData() {
		System.out.println("\n\n##Greedy Approach With Shifting Queries Test -- Elif's data");
		
		String graphPath = "data/graphs/Elif.txt";
		String queriesPath = "data/queries/Elif200.txt";
		
		graph = new MyVariableGraph(graphPath);
		greedySeqDijkstra = new GreedySequentialDijkstra(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
//		int expectedNumberOfFailures = 5;															
//		int expectedTravelTimeOfTheRest = 65858;													// old result using map {5, 65858}
//		int expectedTravelTimeOfTheRest = 66004;													
																									// expected result using set (without shifting) {5, 66004}
																									// expected result withShift {0, 67567}
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = greedySeqDijkstra.process(queryHandler.getQueries(), capacityAware);
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
		System.out.println(greedySeqDijkstra.evaluate(paths));
//		System.out.print("maximumWaitingTime = ");
//		System.out.println(greedySeqDijkstra.getMaxWaitingTime(paths));
//		seqDijkstra.showLoad();	
	
	}
	
//##############################################
	
	//@Test
	public void ChrisData() {																		//only 1836 (time x + 29)
		System.out.println("\n\n##Greedy Approach With Shifting Queries Test -- Chris's data");
		
		String graphPath = "data/graphs/Chris_graph_fixed.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		graph = new MyVariableGraph(graphPath);
		greedySeqDijkstra = new GreedySequentialDijkstra(graph);
		queryHandler = new QueryHandler(graph, queriesPath);
		
//		int expectedNumberOfFailures = 5;															
//		int expectedTravelTimeOfTheRest = 65858;													// old result using map {5, 65858}
//		int expectedTravelTimeOfTheRest = 66004;													
																									// expected result using set (without shifting) {5, 66004}
																									// expected result withShift {0, 67567}
		
		boolean capacityAware = true;
		List<Pair<Query, Path>> queriesWithSolutions = greedySeqDijkstra.process(queryHandler.getQueries(), capacityAware);
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
//		
//		assert seqDijkstra.evaluate(paths).first() == expectedNumberOfFailures;
//		assert seqDijkstra.evaluate(paths).second() == expectedTravelTimeOfTheRest;
		
		System.out.println("##Greedy::");
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(greedySeqDijkstra.evaluate(paths));
//		System.out.print("maximumWaitingTime = ");
//		System.out.println(greedySeqDijkstra.getMaxWaitingTime(paths));
//		seqDijkstra.showLoad();	
		
		DijkstraBenchmark db = new DijkstraBenchmark(graph);
		List<Pair<Query, Path>> benchmarkQueriesWithSolutions = db.process(queryHandler.getQueries());
		
		List<Path> benchmarkPaths = new ArrayList<Path>();
		for (int i = 0; i < benchmarkQueriesWithSolutions.size(); ++i) {
			Path solution = benchmarkQueriesWithSolutions.get(i).second();
			benchmarkPaths.add(solution);
			if (i < 10) {
				System.out.println("QueryId = " + benchmarkQueriesWithSolutions.get(i).first().getId() +
						" {" + benchmarkQueriesWithSolutions.get(i).first().first() + ", " + benchmarkQueriesWithSolutions.get(i).first().second() + "}" +
						" Solution = " + solution);
			}
		}
		
		System.out.println("##Benchmark::");
		System.out.print("{nFailed, totalTravelTime} = ");
		System.out.println(greedySeqDijkstra.evaluate(benchmarkPaths));
//		System.out.print("maximumWaitingTime = ");
//		System.out.println(greedySeqDijkstra.getMaxWaitingTime(benchmarkPaths));
	}

}
