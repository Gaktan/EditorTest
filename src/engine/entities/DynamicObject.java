package engine.entities;

import engine.shapes.DynamicQuad;

public class DynamicObject extends EntityActor {

	public DynamicObject(DynamicQuad shape) {
		super(shape);
	}

	@Override
	public DynamicQuad getShape() {
		return (DynamicQuad) shape;
	}
}
