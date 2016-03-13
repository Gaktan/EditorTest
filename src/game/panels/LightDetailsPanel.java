package game.panels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import engine.entities.Light;

public class LightDetailsPanel extends EntityDetailsPanel {

	private static final long serialVersionUID = -3179180788480614676L;

	// Radius
	protected JPanel radiusPanel;
	protected SliderPanel<Light> lightRadius;

	// Color
	protected JPanel colorPanel;
	protected VectorPanel colorR;
	protected VectorPanel colorG;
	protected VectorPanel colorB;

	public LightDetailsPanel(Light light) {
		super(light);

		radiusPanel = new JPanel();
		radiusPanel.setLayout(new BoxLayout(radiusPanel, BoxLayout.PAGE_AXIS));
		TitledBorder titleRadius = BorderFactory.createTitledBorder("Radius");
		radiusPanel.setBorder(titleRadius);
		add(radiusPanel);

		colorPanel = new JPanel();
		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.PAGE_AXIS));
		TitledBorder titleColor = BorderFactory.createTitledBorder("Color");
		colorPanel.setBorder(titleColor);
		add(colorPanel);

		lightRadius = new SliderPanel<Light>(light, "R ", width, 0f, 10f) {
			private static final long serialVersionUID = 8501206006988325767L;

			@Override
			protected void init() {
				slider.setValue(light.radius);
			}

			@Override
			protected void updatedSlider() {
				light.radius = slider.getFloatValue();
			}
		};
		lightRadius.init();
		radiusPanel.add(lightRadius);

		colorR = new VectorPanel(light.color, 0, "R ", width, 0f, 1f);
		colorR.init();
		colorPanel.add(colorR);
		colorG = new VectorPanel(light.color, 1, "G ", width, 0f, 1f);
		colorG.init();
		colorPanel.add(colorG);
		colorB = new VectorPanel(light.color, 2, "B ", width, 0f, 1f);
		colorB.init();
		colorPanel.add(colorB);
	}
}
