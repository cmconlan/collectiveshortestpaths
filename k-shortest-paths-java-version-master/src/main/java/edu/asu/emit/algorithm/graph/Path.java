/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package edu.asu.emit.algorithm.graph;

import java.util.List;
import java.util.Vector;

import edu.asu.emit.algorithm.graph.abstraction.BaseElementWithWeight;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 * The class defines a path in graph.
 * 
 * @author yqi
 * @author Tomasz Janus
 */
public class Path implements BaseElementWithWeight, Comparable<Path> {
	
	private List<BaseVertex> vertexList = new Vector<BaseVertex>();
	private List<Integer> arrivalTimes = new Vector<Integer>();
	private int weight;
	private double delta = 0d;
	
	private int waitingTime = 0;
	
	public Path() { }
	
	public Path(List<BaseVertex> vertexList, List<Integer> arrivalTimes) {							// assume nonEmpty lists
		this.vertexList = vertexList;
		this.weight = arrivalTimes.get(arrivalTimes.size() - 1) - arrivalTimes.get(0);
		this.arrivalTimes = arrivalTimes;
	}

	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public List<Integer> getArrivalTimes() {
		return arrivalTimes;
	}
	
	public List<BaseVertex> getVertexList() {
		return vertexList;
	}
	
	public BaseVertex getFirst() {
		if (!vertexList.isEmpty())
			return vertexList.get(0);
		else 
			return null;
	}
	
	public BaseVertex getLast() {
		if (!vertexList.isEmpty())
			return vertexList.get(vertexList.size() - 1);
		else 
			return null;
	}
	
	@Override
	public boolean equals(Object right) {
		
		if (right instanceof Path) {
			Path rPath = (Path) right;
			return vertexList.equals(rPath.vertexList);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return vertexList.hashCode();
	}
	
	public String toString() {
		return vertexList.toString() + " : " + weight;
	}
	
	public int size() {
		return vertexList.size();
	}
	
	public BaseVertex get(int i) {
		return vertexList.get(i);
	}

	public int compareTo(Path rhs) {																// <- Path.compareTo (w.r.t. absolute length)
		if (getWeight() < rhs.getWeight()) 
			return -1;
		else if (getWeight() == rhs.getWeight()) 
			return 0;
		return 1;
	}
	
	public void setDelta(double newDelta) {
		delta = newDelta;
	}
	
	public double getDelta() {
		return delta;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	
//	public int compareTo(Path rhs) {																// <- I refer to this with Path.compareToDelta
//		if (getDelta() < rhs.getDelta()) 
//			return -1;
//		else if (getDelta() == rhs.getDelta()) 
//			return 0;
//		return 1;
//	}
}
