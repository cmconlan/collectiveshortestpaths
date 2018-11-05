package edu.asu.emit.algorithm.utils;

public class EdgeTime implements Comparable<EdgeTime>{

	Pair<Edge, Integer> pair;
	
	public EdgeTime(Edge edge, Integer time) {
		pair = new Pair<Edge, Integer> (edge, time);
	}
	
	public Edge first() {
		return pair.first();
	}
	
	public Integer second() {
		return pair.second();
	}
	
	
	public int compareTo(EdgeTime rhs) {
		if (second().compareTo(rhs.second()) != 0) 
			return second().compareTo(rhs.second());
		else 
			return first().compareTo(rhs.first());		
	}
	
	public String toString() {
		return "{" + first() + ", " + second() + "}";
	}
	
	//necessary for hashMap
	public int hashCode() {
        return pair.hashCode();
    }
		
	//necessary for hashMap
	public boolean equals(Object obj) {
		if (!(obj instanceof EdgeTime)) {
        	return false;
		}
        EdgeTime rhs = (EdgeTime) obj;
		return first().equals(rhs.first()) && second().equals(rhs.second());
	}
	
}
