package engine.shapes;

import engine.shapes.ShaderProgram.Uniform;
import engine.util.TextureInfo;

public class ShapeInstancedSprite extends ShapeInstancedQuadTexture {

	protected int spritesWidth, spritesHeight;

	public ShapeInstancedSprite(String texture, int spritesWidth, int spritesHeight) {
		super(texture);

		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	protected ShapeInstancedSprite(TextureInfo texture, int spritesWidth, int spritesHeight) {
		super(texture);

		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	@Override
	public ShapeInstancedSprite copy() {
		ShapeInstancedSprite shape = new ShapeInstancedSprite(texture, spritesWidth, spritesHeight);
		return shape;
	}

	@Override
	public void preRender() {
		super.preRender();
		ShaderProgram.getCurrentProgram().setUniform(Uniform.imageInfo, texture.getWidth(), texture.getHeight(),
				spritesWidth, spritesHeight);
	}

	@Override
	public void postRender() {
		super.postRender();
		ShaderProgram.getCurrentProgram().setUniform(Uniform.imageInfo, 1f, 1f, 1f, 1f);
	}
}
