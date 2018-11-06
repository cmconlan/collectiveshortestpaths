package edu.asu.emit.algorithm.utils;

import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 * 
 * @author Tomasz Janus
 * (another) wrapper class for pair<BaseVertex, BaseVertex>
 */

public class Query implements Comparable<Query>{
	
	private static int queriesNum = 0;
	private final Pair<BaseVertex, BaseVertex> pair;												// shouldn't change during program execution
	private int startTime;
	private final int ID;																			// in case of multipleQueries with the same 
																									// source, destination, and startTime
	
	
	public Query(BaseVertex o1, BaseVertex o2, int startTime) {
		pair = new Pair<BaseVertex, BaseVertex>(o1, o2);
		this.startTime = startTime;
		ID = queriesNum++;
	}
	
	public BaseVertex first() {
		return pair.first();
	}
	
	public BaseVertex second() {
		return pair.second();
	}
	
	
	public String toString() {
		return "Query{" + first() + ", " + second() + "}";
	}
	
	public int getId() {
		return ID;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	//necessary for hashMap
	public int hashCode() {
        return pair.hashCode();																		// [TODO] at least test this function
    }
		
		//necessary for hashMap
	public boolean equals(Object obj) {
		if (!(obj instanceof Query)) {
        	return false;
		}
        Query rhs = (Query) obj;
		return this.getId() == rhs.getId();															// it is enough to compare ids (integer comparison == works)
	}
	
	public int compareTo(Query rhs) {																// integer comparison <, ==, > works here
		if (getStartTime() < rhs.getStartTime()) {
			return -1;
		} else if (getStartTime() == rhs.getStartTime()) {
			if (getId() < rhs.getId()) 
				return -1;
			else if (getId() == rhs.getId()) 
				return 0;
			else 
				return 1;
		} else 
			return 1;
	}
}
