package uk.ac.warwick.queries;

import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.utils.Pair;

/**
 * 
 * @author Tomasz Janus
 *  a tuple (BaseVertex from, BaseVertex to, int startTime) 
 */

public class Query implements Comparable<Query>{
	
	private static int queriesNum = 0;
	private final Pair<BaseVertex, BaseVertex> pair;												// shouldn't change during program execution
	private int startTime;
	private final int ID;																			// in case of multipleQueries with the same 
																									// source, destination, and startTime
	
	private int initialStartTime;																	// Some queries are shifted in time
																									// this variable keeps track of startTime of the first query of a given type
	
	private int expectedTravelTime;																	// need for collectiveness
	private int expectedNumerOfNodesOnTheWay;														// need for collectiveness
	
	
	public Query(BaseVertex o1, BaseVertex o2, int startTime) {
		pair = new Pair<BaseVertex, BaseVertex>(o1, o2);
		this.startTime = startTime;
		this.initialStartTime = startTime;
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
        return pair.hashCode()/2 + startTime/2;														// [TODO] at least test this function
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

	public int getInitialStartTime() {
		return initialStartTime;
	}

	public void setInitialStartTime(int initialStartTime) {
		this.initialStartTime = initialStartTime;
	}

	// need for collectiveness
	
	public int getExpectedTravelTime() {
		return expectedTravelTime;
	}

	public void setExpectedTravelTime(int expectedTravelTime) {
		this.expectedTravelTime = expectedTravelTime;
	}

	public int getExpectedNumerOfNodesOnTheWay() {
		return expectedNumerOfNodesOnTheWay;
	}

	public void setExpectedNumerOfNodesOnTheWay(int expectedNumerOfNodesOnTheWay) {
		this.expectedNumerOfNodesOnTheWay = expectedNumerOfNodesOnTheWay;
	}
}
