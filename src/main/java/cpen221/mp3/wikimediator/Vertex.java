package cpen221.mp3.wikimediator;

/**
 * Represents a graph vertex. Each vertex has an associated name.
 * No two vertices in the same graph should have the same name.
 *
 * AF:
 *    Represents a named vertex on a directed unweighted graph.
 *    The name will be the title of a wikipedia page
 *
 * RI:
 *    Names are stored as strings, and are mutable
 *
 */
public class Vertex {
	private String name;

	/**
	 * Create a new vertex
	 *
	 * @param name is a name for the vertex
	 */
	public Vertex(String name) {
		this.name = name;
	}
}
