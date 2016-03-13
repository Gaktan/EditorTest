package engine.entities;

import engine.shapes.DynamicQuad;
import engine.shapes.ShaderProgram;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.ShapeQuadTexture;
import engine.util.Matrix4;
import engine.util.TextureInfo;
import engine.util.Vector3;

public class Light extends DynamicObject {

	public float radius;
	public int resolution;
	protected ShapeQuadTexture hiddenShape;

	public Light(Vector3 position, float radius, Vector3 color) {
		super(new DynamicQuad("light.png"));

		this.radius = radius;

		this.position = position;
		position.addZ(0.1f);
		this.color = color;
		resolution = 512;
	}

	@Override
	public void dispose() {
		super.dispose();
		hiddenShape.dispose();
	}

	@Override
	public void render() {
	}

	public void realRender() {

		Vector3 oldColor = new Vector3(color);
		color.add(0.1f);
		color.scale(1.5f);
		super.render();
		color.set(oldColor);

		ShaderProgram oldProgram = ShaderProgram.getCurrentProgram();
		ShaderProgram.setCurrentProgram(ShaderProgram.getProgram("light"));
		ShaderProgram.getCurrentProgram().setUniform("u_resolution", 512, 512);
		Matrix4 model = Matrix4.createModelMatrix(position, new Vector3(20f * radius, 20f * radius, 1f));
		ShaderProgram.getCurrentProgram().setUniform(Uniform.model, model);
		ShaderProgram.getCurrentProgram().setUniform(Uniform.color, color);

		hiddenShape.preRender();
		hiddenShape.render();
		hiddenShape.postRender();

		ShaderProgram.setCurrentProgram(oldProgram);
	}

	float total = 0f;

	@Override
	public boolean update(float dt) {
		total += dt;

		/*
		color.setX(0.5f * (1f + MathUtil.sin(0.007f * total)));
		color.setY(0.5f * (1f + MathUtil.sin(0.003f * total)));
		color.setZ(0.5f * (1f + MathUtil.sin(0.0013f * total)));
		*/

		return super.update(dt);
	}

	public void setTexture(TextureInfo texture) {
		if (hiddenShape != null) {
			hiddenShape.dispose();
		}
		hiddenShape = new ShapeQuadTexture(texture);
	}

	public float getRadius() {
		return radius;
	}
}
