package game.panels;

import engine.entities.Entity;
import engine.entities.Light;

public class DetailsPanelSelector {

	static final EmptyDetailsPanel<? extends Object> select(Object o) {
		EmptyDetailsPanel<? extends Object> panel;
		if (o instanceof Light) {
			panel = new LightDetailsPanel((Light) o);
		}
		else if (o instanceof Entity) {
			panel = new EntityDetailsPanel((Entity) o);
		}
		else {
			panel = new EmptyDetailsPanel<Object>(null);
		}

		return panel;
	}

}
