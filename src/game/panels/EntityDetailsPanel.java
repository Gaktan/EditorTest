package game.panels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import engine.entities.Entity;
import game.game.GameEditor;

public class EntityDetailsPanel extends EmptyDetailsPanel<Entity> {

	private static final long serialVersionUID = 5998329076367700705L;

	// Pos
	protected JPanel positionPanel;
	protected VectorPanel posX;
	protected VectorPanel posY;
	protected VectorPanel posZ;

	public EntityDetailsPanel(Entity entity) {
		super(entity);

		positionPanel = new JPanel();
		positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.PAGE_AXIS));
		TitledBorder title = BorderFactory.createTitledBorder("Position");
		positionPanel.setBorder(title);
		add(positionPanel);

		posX = new VectorPanel(getObject().position, 0, "X ", width, -10f, 10f);
		positionPanel.add(posX);
		posY = new VectorPanel(getObject().position, 1, "Y ", width, -10f, 10f);
		positionPanel.add(posY);
		posZ = new VectorPanel(getObject().position, 2, "Z ", width, GameEditor.getInstance().camera.getzNear(),
				GameEditor.getInstance().camera.getzFar());
		positionPanel.add(posZ);
	}

}
