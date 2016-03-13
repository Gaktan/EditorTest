package game.panels;

import javax.swing.JSlider;

public class FloatSlider extends JSlider {

	private static final long serialVersionUID = -7612153658442124686L;

	protected int scale;

	public FloatSlider(float min, float max, float value, int decimals) {
		super();

		scale = 1;
		for (int i = 0; i < decimals; i++) {
			scale *= 10;
		}

		setMinimum(min);
		setMaximum(max);
		setValue(value);
	}

	public void setMinimum(float n) {
		super.setMinimum((int) (n * scale));
	}

	public void setMaximum(float n) {
		super.setMaximum((int) (n * scale));
	}

	public void setValue(float n) {
		super.setValue((int) (n * scale));
	}

	public float getFloatValue() {
		return (float) super.getValue() / scale;
	}

}
