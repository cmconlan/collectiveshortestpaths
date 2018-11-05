package edu.asu.emit.algorithm.utils;

import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 * 
 * @author Tomasz Janus
 * (another) wrapper class for pair<BaseVertex, BaseVertex>
 */

public class Query implements Comparable<Query>{
	
	Pair<BaseVertex, BaseVertex> pair;
	
	public Query(BaseVertex o1, BaseVertex o2) {
		pair = new Pair<BaseVertex, BaseVertex>(o1, o2);
	}
	
	public BaseVertex first() {
		return pair.first();
	}
	
	public BaseVertex second() {
		return pair.second();
	}
	
	public int compareTo(Query rhs) {
		if (first().compareTo(rhs.first()) != 0) 
			return first().compareTo(rhs.first());
		else 
			return second().compareTo(rhs.second());
	}
	
	
	public String toString() {
		return "Query{" + first() + ", " + second() + "}";
	}
}
