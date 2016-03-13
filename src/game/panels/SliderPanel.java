package game.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class SliderPanel<T extends Object> extends JPanel {

	private static final long serialVersionUID = 1304597752978190216L;
	protected static final DecimalFormat df;

	static {
		DecimalFormatSymbols dfm = new DecimalFormatSymbols();
		dfm.setDecimalSeparator('.');
		df = new DecimalFormat("0.####", dfm);
	}

	protected JLabel label;
	protected FloatSlider slider;
	protected JTextField text;
	private T object;

	public SliderPanel(T object, String labelName, int width, float min, float max) {
		super(new BorderLayout());
		this.object = object;

		label = new JLabel(labelName);
		add(label, BorderLayout.LINE_START);

		slider = new FloatSlider(min, max, 0, 3);
		slider.setPreferredSize(new Dimension((int) (width / 1.5), getHeight()));
		add(slider, BorderLayout.LINE_END);

		text = new JTextField();
		text.setPreferredSize(new Dimension(width / 5, getHeight()));
		add(text, BorderLayout.CENTER);

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateText();
				updatedSlider();
			}
		});
		slider.setEnabled(true);
		text.setEnabled(true);

		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String typed = text.getText();
					slider.setValue(0);
					if (!typed.matches("(-)?\\d+((\\.)\\d*)?")) {
						return;
					}
					float value = Float.parseFloat(typed);
					slider.setValue(value);
				}
			};
		});
	}

	protected void init() {
		// code to initialize
	}

	protected void updatedSlider() {
		// Code when slider value changes here
	}

	private void updateText() {
		text.setText(df.format(slider.getFloatValue()));
	}

	public T getObject() {
		return object;
	}
}
