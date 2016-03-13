package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.util.TextureInfo;
import engine.util.Vector3;

public class DynamicQuad extends TexturedShape {

	public ArrayList<Vertex> verticesList;

	public DynamicQuad(TextureInfo texture) {
		super(texture);
	}

	public DynamicQuad(String texture) {
		super(texture);
	}

	@Override
	public void render() {
		int offset = 0;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		for (Vertex v : verticesList) {
			if (v.changed) {
				v.changed = false;
				FloatBuffer fb = BufferUtils.createFloatBuffer(Vertex.SIZE);
				v.position.store(fb);
				fb.put(v.uvX);
				fb.put(v.uvY);
				fb.flip();
				GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, fb);
			}

			offset += Vertex.SIZE * FLOAT_SIZE;
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL11.glDrawElements(GL11.GL_TRIANGLES, 3 * 2, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	protected void setAttribs() {
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * FLOAT_SIZE, 0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);
	}

	@Override
	protected void init() {
		verticesList = new ArrayList<Vertex>();

		// front 1
		verticesList.add(new Vertex(-0.5f, -0.5f, 0f, 0f, 1f));
		verticesList.add(new Vertex(-0.5f, +0.5f, 0f, 0f, 0f));
		verticesList.add(new Vertex(+0.5f, +0.5f, 0f, 1f, 0f));
		verticesList.add(new Vertex(+0.5f, -0.5f, 0f, 1f, 1f));

		FloatBuffer vertices = BufferUtils.createFloatBuffer(verticesList.size() * Vertex.SIZE);
		for (Vertex v : verticesList) {
			v.position.store(vertices);
			vertices.put(v.uvX);
			vertices.put(v.uvY);
		}
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(2 * 3);
		indices.put(new int[] {//
		0, 1, 2,//
		0, 2, 3 //
		});
		indices.flip();

		createArrayObject();
		loadVertices(vertices);
		loadIndices(indices);
		setAttribs();

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}

	@Override
	protected void loadVertices(FloatBuffer vertices) {
		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_DYNAMIC_DRAW);
	}

	public boolean pointInPolygon(Vector3 position, Vector3 point) {
		int i, j, nvert = verticesList.size();
		boolean c = false;

		for (i = 0, j = nvert - 1; i < nvert; j = i++) {
			Vector3 a = verticesList.get(i).position.getAdd(position);
			Vector3 b = verticesList.get(j).position.getAdd(position);
			if (((a.getY() >= point.getY()) != (b.getY() >= point.getY()))
					&& (point.getX() <= (b.getX() - a.getX()) * (point.getY() - a.getY()) / (b.getY() - a.getY())
							+ a.getX()))
				c = !c;
		}

		return c;
	}
}
