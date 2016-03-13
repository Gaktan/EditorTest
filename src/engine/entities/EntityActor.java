package engine.entities;

import engine.shapes.ShaderProgram;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.Shape;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * Entity that has a textured shape, a color, rotation and scale
 *
 * @author Gaktan
 */
public class EntityActor extends Entity {

	protected Shape shape;
	public Vector3 color;

	public EntityActor(Shape shape) {
		super();

		this.shape = shape;
		color = new Vector3(1f);
	}

	@Override
	public void dispose() {
		super.dispose();
		shape.dispose();
	}

	@Override
	public void render() {
		shape.preRender();
		setUniforms();
		shape.render();
		shape.postRender();
	}

	/**
	 * Sets the uniforms to be used in the shader. Do not call this
	 */
	public void setUniforms() {
		ShaderProgram.getCurrentProgram().setUniform(Uniform.color, color);
		Matrix4 model = Matrix4.createModelMatrix(position);
		ShaderProgram.getCurrentProgram().setUniform(Uniform.model, model);
		ShaderProgram.getCurrentProgram().setUniform(Uniform.spriteNumber, -1f);
	}

	public AABB getAABB() {
		AABBRectangle rect = new AABBRectangle(new Vector3(position), new Vector3(1f));
		return rect;
	}

	public Shape getShape() {
		return shape;
	}
}
