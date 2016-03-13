package engine.shapes;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;

import engine.util.TextureInfo;

public abstract class InstancedTexturedShape extends TexturedShape {

	protected int instancedVBO;

	public InstancedTexturedShape(TextureInfo texture) {
		super(texture);
	}

	public InstancedTexturedShape(String texture) {
		super(texture);
	}

	@Override
	public void dispose() {
		GL15.glDeleteBuffers(instancedVBO);
		super.dispose();
	}

	@Override
	public void render() {
		// You should be calling render(int amount) instead
	}

	public abstract void render(int amount);

	public abstract void setData(FloatBuffer data);
}
