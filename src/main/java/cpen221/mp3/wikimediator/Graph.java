package cpen221.mp3.wikimediator;

import java.util.*;

/**
 * Represents a graph with vertices of type V.
 *
 * Abstraction function:
 *      Represents an undirected, weighted graph consisting of verticies and optional
 *      edges between them. A Graph may contain seperate graphs that are not connected
 *      by any edges.
 *
 * Representation invariant:
 *      The vertex and edge lists do not contain any duplicates.
 *      All edges have positive or zero weights.
 *      Verticies are all indexed differently.
 *      eList contains all edges in the graph
 *      vList contains all verticies in the graph
 *
 * @param <V> represents a vertex type
 */
public class Graph<V extends Vertex, E extends Edge<V>> {
	private List<V> vList;
	private List<E> eList;

	/**
	 * Creates a new instance of Graph.
	 * <p>
	 * This constructor initializes eList and vList, but does not add any elements to them.
	 */
	public Graph() {
		vList = new ArrayList<V>();
		eList = new ArrayList<E>();
	}
}