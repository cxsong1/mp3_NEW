package cpen221.mp3.wikimediator;

import java.util.NoSuchElementException;

/**
 * Describes an edge that connects to verticies in an unweighted, directed graph.
 *
 * AF:
 *    Describes a path between two wikipedia pages, stored as two vertices
 *
 * RI:
 *    The verticies that comprise the edge cannot be null
 *    Endpoints are stored as V1 and V2
 *
 */
public class Edge<V extends Vertex> {

	private V v1;
	private V v2;

	public Edge(V v1, V v2) {
		if (v1 == null || v2 == null) {
			throw new IllegalArgumentException("Vertices cannot be null");
		}

		this.v1 = v1;
		this.v2 = v2;
	}
}
