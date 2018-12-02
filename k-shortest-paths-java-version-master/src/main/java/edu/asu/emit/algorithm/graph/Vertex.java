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

import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 * The class defines a vertex in the graph
 * 
 * @author yqi
 * @author Tomasz Janus
 */
public class Vertex implements BaseVertex {
	
	private static int currentVertexNum = 0; // Uniquely identify each vertex
	private final int ID = currentVertexNum++;
	private int weight = 0;
	
	public int getId() {
		return ID;
	}

	public String toString() {
		return "" + ID;
	}

	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int status) {
		weight = status;
	}
	
	public int compareTo(BaseVertex rVertex) {														// FIXED (there was comparator using Dijkstra times)
		if (getId() > rVertex.getId()) 
			return 1;
		else if (getId() == rVertex.getId()) 
			return 0;
		return -1;
	}
	
	public static void reset() {
		currentVertexNum = 0;
	}																								// we don't need to @override hashcode and equals methods, 
																									// since vertices are only created by graph hence 
																									// Object implementations work well here
}
