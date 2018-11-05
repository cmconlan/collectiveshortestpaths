package uk.ac.warwick.heuristics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.asu.emit.algorithm.graph.abstraction.BaseGraph;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.utils.Pair;
import edu.asu.emit.algorithm.utils.Query;

/**
 * 
 * @author Tomasz Janus
 * @email t.janus@warwick.ac.uk
 *
 * this class transform queries from integers to BaseVertices
 * it assumes that queries are list of pair of integers 
 * i.e., each line in the query file is a pair of integers 
 * query.from query.to
 */

public class QueryHandler {																			
	private List<Query> queries;
	private BufferedReader buffRead;
	
	QueryHandler(final BaseGraph graph, String dataFileName) {
		File f = null;
		FileReader input;
		queries = new ArrayList<Query>();
		try {
			f = new File(dataFileName);																// throws nullPointerException (if dataFileName is null)
			input = new FileReader(f);																// throws FileNotFoundException
			buffRead = new BufferedReader(input);
			String line = buffRead.readLine();														// throws IOException
					
			while (line != null) {
				// 2.1 skip the empty line
				if (line.trim().equals("")) {
					line = buffRead.readLine();
					continue;
				}
				
				String[] items = line.trim().split("\\s+");
				// 2.2. parse source, sink pairs
				try {
					int source = Integer.parseInt(items[0]);											// throws IndexOutOfBoundException
					int sink = Integer.parseInt(items[1]);												// throws IndexOutOfBoundException
				
				queries.add(new Query(graph.getVertex(source), graph.getVertex(sink)));
				} catch (IndexOutOfBoundsException e) {
					System.err.println("WRONG "+ f.getName() + " FORMAT!");
				}
				
				line = buffRead.readLine();
			}
		}catch (FileNotFoundException e) {
			System.err.println(f.getAbsolutePath() + " DOES NOT EXIST!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Query> getQueries() {
		return queries;
	}

}
