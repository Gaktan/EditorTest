package engine.shapes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import engine.util.TextureInfo;
import engine.util.TextureUtil;

public abstract class TexturedShape extends Shape {

	protected TextureInfo texture;

	public TexturedShape(TextureInfo texture) {
		super();
		this.texture = texture;
	}

	public TexturedShape(String texture) {
		super();
		this.texture = TextureUtil.loadTexture(texture);
	}

	@Override
	public void postRender() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void preRender() {
		GL30.glBindVertexArray(VAO);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
	}
}
