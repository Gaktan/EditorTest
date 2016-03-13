package game.panels;

import java.awt.Dimension;

import javax.swing.JPanel;

public class EmptyDetailsPanel<T extends Object> extends JPanel {

	private static final long serialVersionUID = -8126997969837977216L;
	protected static int width;

	private T object;

	public EmptyDetailsPanel(T object) {
		super();
		setPreferredSize(new Dimension(width, 0));
		setObject(object);
	}

	private void setObject(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public static void setWidth(int _width) {
		width = _width;
	}
}
