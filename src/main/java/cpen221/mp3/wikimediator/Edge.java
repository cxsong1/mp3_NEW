package cpen221.mp3.wikimediator;

import java.util.NoSuchElementException;

/**
 * Describes an edge that connects to verticies in a weighted, undirected graph.
 *
 * AF:
 *    Describes an edge that connects two verticies in a weighted, undirected graph.
 * RI:
 *    The verticies spanned by the edge are not the same (distinct endpoints)
 *    The edge's weight is greater than zero (non-negative weights)
 *    The verticies that comprise the edge cannot be null
 *    Two edges with identical endpoints have the same hashcode and are equal
 *    Endpoints are stored as V1 and V2
 *    Length and endpoints are immutable
 *    length is stored as an int
 *
 */
public class Edge<V extends Vertex> {

	private V v1;
	private V v2;
	private int length;

	public Edge(V v1, V v2) {
		this(v1, v2, 1);
	}

	public Edge(V v1, V v2, int length) {
		if (v1 == null || v2 == null) {
			throw new IllegalArgumentException("Vertices cannot be null");
		}
		if (v1.equals(v2)) {
			throw new IllegalArgumentException("The same vertex cannot be at both ends of an edge");
		}
		if (length < 0) {
			throw new IllegalArgumentException("Edge weight cannot be negative");
		}
		this.v1 = v1;
		this.v2 = v2;
		this.length = length;
	}
}
