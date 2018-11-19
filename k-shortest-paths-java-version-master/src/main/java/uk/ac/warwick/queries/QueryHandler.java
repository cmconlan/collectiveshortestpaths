package uk.ac.warwick.queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;

/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * This class transform queries from integers to BaseVertices
 * it assumes that queries are list of pair of integers 
 * i.e., each line in the query file is a pair of integers 
 * query.from query.to
 * 
 * This class also sorts queries with respect to startTime.
 */

public class QueryHandler {																			
	private Set<Query> queries;
	private BufferedReader buffRead;
	
	public QueryHandler(final BaseGraph graph, String dataFileName) {
		File file  = new File(dataFileName);															
		queries = new TreeSet<Query>();																// we want queries to be ordered w.r.t. queryTime
		try {														
			FileReader input = new FileReader(file);												// throws FileNotFoundException
			buffRead = new BufferedReader(input);
			String line = buffRead.readLine();														// throws IOException
					
			while (line != null) {
				// skip the empty line
				if (line.trim().equals("")) {
					line = buffRead.readLine();
					continue;
				}
				
				String[] items = line.trim().split("\\s+");
				// parse source, sink (,startTime) pairs (triplets)
				try {
					int source = Integer.parseInt(items[0]);										// throws IndexOutOfBoundException
					int sink = Integer.parseInt(items[1]);											// throws IndexOutOfBoundException
					int startTime = 0;
					if (items.length > 2) {
						startTime = Integer.parseInt(items[2]);
					}
				Query query = new Query(graph.getVertex(source), graph.getVertex(sink), startTime);
				queries.add(query);
				
				} catch (IndexOutOfBoundsException e) {
					System.err.println("WRONG "+ file.getName() + " FORMAT!");
					System.out.println("Expected line format %d %d = source, sink or %d %d %d = source, sink, startTime.");
				}
				
				line = buffRead.readLine();
			}
		}catch (FileNotFoundException e) {
			System.err.println(file.getAbsolutePath() + " DOES NOT EXIST!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Set<Query> getQueries() {
		return queries;
	}

}
