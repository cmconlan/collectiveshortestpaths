package edu.asu.emit.algorithm.graph.abstraction;

import java.util.Map;

import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.utils.Edge;

public interface BaseDijkstraShortestPathAlg {
	public void clear();
	
	public void setLoad(Map<Edge, int[]> load);
	public int getStartTime();
	public void setStartTime(int startTime);
	public Path getShortestPath(BaseVertex sourceVertex, BaseVertex sinkVertex);
}
