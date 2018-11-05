package edu.asu.emit.algorithm.utils;

import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 * 
 * @author Tomasz Janus
 * wrapper class for pair<BaseVertex, BaseVertex>
 * 
 */

public class Edge implements Comparable<Edge>{
	
	Pair<BaseVertex, BaseVertex> pair;
	
	public Edge(BaseVertex o1, BaseVertex o2) {
		pair = new Pair<BaseVertex, BaseVertex>(o1, o2);
	}
	
	public BaseVertex first() {
		return pair.first();
	}
	
	public BaseVertex second() {
		return pair.second();
	}
	
	public int compareTo(Edge rhs) {
		if (first().compareTo(rhs.first()) != 0) 
			return first().compareTo(rhs.first());
		else 
			return second().compareTo(rhs.second());
	}
	
	public String toString() {
		return "Edge{" + first() + ", " + second() + "}";
	}
	
	
	//necessary for hashMap
	public int hashCode() {
        return pair.hashCode();
    }
	
	//necessary for hashMap
	public boolean equals(Object obj) {
		if (!(obj instanceof Edge)) {
        	return false;
		}
        Edge rhs = (Edge) obj;
		return first().equals(rhs.first()) && second().equals(rhs.second());
	}
}
