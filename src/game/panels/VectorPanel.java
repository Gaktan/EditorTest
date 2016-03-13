package game.panels;

import engine.util.Vector3;

public class VectorPanel extends SliderPanel<Vector3> {

	private static final long serialVersionUID = 1755006592763864799L;

	protected int d;

	public VectorPanel(Vector3 vector, int d, String labelName, int width, float min, float max) {
		super(vector, labelName, width, min, max);

		this.d = d;
		init();
	}

	@Override
	protected void init() {
		Vector3 position = getObject();
		if (d == 0) {
			slider.setValue(position.getX());
		}
		else if (d == 1) {
			slider.setValue(position.getY());
		}
		else if (d == 2) {
			slider.setValue(position.getZ());
		}
	}

	@Override
	protected void updatedSlider() {
		Vector3 position = getObject();
		if (d == 0) {
			position.setX(slider.getFloatValue());
		}
		else if (d == 1) {
			position.setY(slider.getFloatValue());
		}
		else if (d == 2) {
			position.setZ(slider.getFloatValue());
		}
	}

}
