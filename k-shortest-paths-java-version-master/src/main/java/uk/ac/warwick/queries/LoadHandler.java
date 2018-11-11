package uk.ac.warwick.queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;
import edu.asu.emit.algorithm.utils.Edge;

public class LoadHandler {																			// [TODO] tests for this class
	private Map<Edge, int[]> load;
	private BufferedReader buffRead;
	
	public LoadHandler(final BaseGraph graph, String dataFileName) {
		File file = null;
		FileReader input;
		load = new HashMap<Edge, int[]>();
		
		try {
			file = new File(dataFileName);															// throws nullPointerException (if dataFileName is null)
			input = new FileReader(file);															// throws FileNotFoundException
			buffRead = new BufferedReader(input);
			String line = buffRead.readLine();														// throws IOException
					
			while (line != null) {
				// skip the empty line
				if (line.trim().equals("")) {
					line = buffRead.readLine();
					continue;
				}
				
				String[] items = line.trim().split("\\s+");
				// parse a tuple (from, to, when, trafficLoad)
				int timeHorizon = 1;
				boolean isFirstLine = true;
				try {
					if (isFirstLine) {
						timeHorizon = Integer.parseInt(items[0]);
						isFirstLine = false;
						
					}
					else {
						int from = Integer.parseInt(items[0]);										// throws IndexOutOfBoundException
						int to = Integer.parseInt(items[1]);										// throws IndexOutOfBoundException
						int when = Integer.parseInt(items[2]);
						int trafficLoad = Integer.parseInt(items[3]);
						
						Edge edge = new Edge(graph.getVertex(from), graph.getVertex(to));
						int edgeCapacity = graph.getEdgeCapacity(edge.first(), edge.second());
						
						if (trafficLoad <= edgeCapacity) { 											// ignore incorrect traffic data (load > capacity) 
							int[] timeMap;
							
							if (load.containsKey(edge)) {
								timeMap = load.get(edge);
								
								if (timeMap.length < when) {												
									timeMap = Arrays.copyOf(timeMap, timeHorizon + 1);				// not the smartest way -- one can do it better,
																									// first loading all the traffic information,
																									// and then creating timeMap once for each edge in the
																									// load data
								}														
							} else {
								timeMap = new int[timeHorizon + 1];  												
							}
							timeMap[when] = trafficLoad;
						}
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println("WRONG "+ file.getName() + " FORMAT!");
					System.out.println("Expected fine format:");
					System.out.println("First line: %d = time horizon (>= all time instances).");
					System.out.println("Lines 2+: %d %d %d %d = edge.from, edge.to, timeInstance, trafficLoad.");
					
				}
				
				line = buffRead.readLine();
			}
		}catch (FileNotFoundException e) {
			System.err.println(file.getAbsolutePath() + " DOES NOT EXIST!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<Edge, int[]> getTrafficLoad() {
		return load;
	}
}
