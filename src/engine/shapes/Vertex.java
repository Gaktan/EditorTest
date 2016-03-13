package engine.shapes;

import java.io.Serializable;

import engine.util.Vector3;

public class Vertex implements Serializable {

	private static final long serialVersionUID = 1254493525944809454L;

	public static final int POSITION_SIZE = 3;
	public static final int UV_SIZE = 2;
	public static final int SIZE = POSITION_SIZE + UV_SIZE;

	public Vector3 position;
	public float uvX;
	public float uvY;

	public transient boolean changed;

	public Vertex(float x, float y, float z, float uvX, float uvY) {
		this(new Vector3(x, y, z), uvX, uvY);
	}

	public Vertex(Vector3 position, float uvX, float uvY) {
		this.position = position;
		this.uvX = uvX;
		this.uvY = uvY;
	}
}
