package engine.shapes;

import engine.shapes.ShaderProgram.Uniform;
import engine.util.TextureInfo;

public class ShapeSprite extends ShapeQuadTexture {

	protected int spritesWidth, spritesHeight;

	public ShapeSprite(String texture, int spritesWidth, int spritesHeight) {
		super(texture);

		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	protected ShapeSprite(TextureInfo texture, int spritesWidth, int spritesHeight) {
		super(texture);

		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	@Override
	public ShapeSprite copy() {
		ShapeSprite shape = new ShapeSprite(texture, spritesWidth, spritesHeight);
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
