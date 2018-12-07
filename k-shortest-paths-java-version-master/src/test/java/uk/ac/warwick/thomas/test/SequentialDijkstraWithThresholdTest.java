package uk.ac.warwick.thomas.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import uk.ac.warwick.heuristics.SequentialDijkstraWithThreshold;
import uk.ac.warwick.queries.Query;
import uk.ac.warwick.queries.QueryHandler;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;
import edu.asu.emit.algorithm.utils.Pair;

public class SequentialDijkstraWithThresholdTest {
	private BaseGraph graph;
	private SequentialDijkstraWithThreshold SeqDijkstraWT;
	private QueryHandler queryHandler;
	
//	@Test
	public void ElifsData() {
		System.out.println("\n\n##Approach With Threshold Test -- Elif's data");
		
		String graphPath = "data/graphs/Elif.txt";
		String queriesPath = "data/queries/Elif200.txt";
		
		graph = new Graph(graphPath);
		queryHandler = new QueryHandler(graph, queriesPath);
		
		
		double d = 0.0005;
		
		for (int k = 1; k <= 5; ++k) {
			SeqDijkstraWT = new SequentialDijkstraWithThreshold(graph, d * k);
			
	//		int expectedNumberOfFailures = 5;														// 0.1 -> Pair{0, 67569}
																									// 0.2+ ->Pair{0, 67574}	==     greedyApproachSolution
	//		int expectedTravelTimeOfTheRest = 65858;												
	//		int expectedTravelTimeOfTheRest = 66004;													
																										
			
			boolean capacityAware = true;
			List<Pair<Query, Path>> queriesWithSolutions = SeqDijkstraWT.process(queryHandler.getQueries(), capacityAware);
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
			System.out.println(SeqDijkstraWT.evaluate(paths));
			System.out.print("maximumWaitingTime = ");
			System.out.println(SeqDijkstraWT.getMaxWaitingTime(paths));
	//		seqDijkstra.showLoad();	
	
		}
	}
	
	
//	0.01 - > {nFailed, totalTravelTime} = Pair{0, 67511}
//	0.02 - > {nFailed, totalTravelTime} = Pair{0, 67511}
//	0.03 - > {nFailed, totalTravelTime} = Pair{0, 67517}
//	0.04 - > {nFailed, totalTravelTime} = Pair{0, 67517}
//	0.05 - > {nFailed, totalTravelTime} = Pair{0, 67540}
//	0.06 - > {nFailed, totalTravelTime} = Pair{0, 67540}
//	0.07 - > {nFailed, totalTravelTime} = Pair{0, 67569}
//	0.08 - > {nFailed, totalTravelTime} = Pair{0, 67569}
//	0.09 - > {nFailed, totalTravelTime} = Pair{0, 67569}
//	0.10 - > {nFailed, totalTravelTime} = Pair{0, 67569}


	//@Test
	public void ChrisData() {																		// Pair{12629, 189732}
		System.out.println("\n\n##Approach With Threshold Test -- Chris's data");
		
		String graphPath = "data/graphs/Chris_graph.txt";
		String queriesPath = "data/queries/Chris_queries.txt";
		
		graph = new Graph(graphPath);
		queryHandler = new QueryHandler(graph, queriesPath);
		
		
		double d = 0.0005;
		
		for (int k = 1; k <= 5; ++k) {
			SeqDijkstraWT = new SequentialDijkstraWithThreshold(graph, d * k);
			
	//		int expectedNumberOfFailures = 5;														// 0.1 -> Pair{0, 67569}
																									// 0.2+ ->Pair{0, 67574}	==     greedyApproachSolution
	//		int expectedTravelTimeOfTheRest = 65858;												
	//		int expectedTravelTimeOfTheRest = 66004;													
																										
			
			boolean capacityAware = true;
			List<Pair<Query, Path>> queriesWithSolutions = SeqDijkstraWT.process(queryHandler.getQueries(), capacityAware);
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
			System.out.println(SeqDijkstraWT.evaluate(paths));
			System.out.print("maximumWaitingTime = ");
			System.out.println(SeqDijkstraWT.getMaxWaitingTime(paths));
	//		seqDijkstra.showLoad();	
	
		}
	}
	
}
