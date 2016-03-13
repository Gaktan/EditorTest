package game.game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import engine.entities.AABBRectangle;
import engine.entities.DynamicObject;
import engine.game.Controls;
import engine.game.Controls.ControlsListener;
import engine.game.Controls.MouseListener;
import engine.game.Game;
import engine.shapes.DynamicQuad;
import engine.shapes.Vertex;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;

public class Controller implements ControlsListener, MouseListener {

	private GameEditor editor;
	public float mouseSensitivity = 0.2f;

	protected Vector3 position;
	protected Vector3 velocity;
	protected Vector3 movement;
	protected Vector3 movementGoal;
	protected AABBRectangle collisionBox;

	protected float leftGoal;
	protected float rightGoal;
	protected float upGoal;
	protected float downGoal;

	// Speed in every directions
	protected static final float FORWARD_SPEED = 0.4f;
	protected static final float SIDEWAYS_SPEED = 0.3f;
	protected static final float UPDOWN_SPEED = 0.6f;

	public Vertex selectedVertex;
	public DynamicObject selectedObject;
	boolean click;
	Vector3 grabPos;

	public Controller(GameEditor editor) {
		this.editor = editor;

		position = editor.camera.position;
		velocity = editor.camera.velocity;

		movement = new Vector3();
		movementGoal = new Vector3();

		Controls.addControlsListener(this);
		Controls.addMouseListener(this);

		grabPos = new Vector3();
	}

	public void dispose() {
		Controls.removeControlsListener(this);
		Controls.removeMouseListener(this);
	}

	public void update(float elapsedTime) {
		float dt = elapsedTime * 0.01f;

		position.addX(velocity.getX() * dt);
		position.addY(velocity.getY() * dt);
		position.addZ(velocity.getZ() * dt);

		movementGoal.set(leftGoal + rightGoal, upGoal + downGoal, 0f);
		dt = elapsedTime * 0.01f;
		movement = MathUtil.approach(movement, movementGoal, dt);

		Vector3 forward = editor.camera.getViewAngle().toVector();
		forward.setY(0f);
		forward.normalize();
		Vector3 right = Matrix4.Y_AXIS.getCross(forward);
		forward.scale(movement.getX());
		right.scale(movement.getZ());
		velocity.set(forward.getAdd(right));
		velocity.setY(movement.getY());

		if (click && selectedObject != null) {
			float z = Mouse.getDWheel();
			if (z < -100f || z > 100f) {
				z = MathUtil.clamp(z, -1f, 1f);
				selectedObject.position.addZ(z);
			}
		}
	}

	public void setCornersPosition() {
		editor.selectedObjectList.clear();
		DynamicQuad shape = new DynamicQuad("door.png");
		for (Vertex v : shape.verticesList) {
			v.position.scale(0.25f);
			v.changed = true;
		}
		for (Vertex v : selectedObject.getShape().verticesList) {
			DynamicObject cube = new DynamicObject(shape);

			cube.position.set(v.position.getAdd(selectedObject.position));
			cube.position.setZ(editor.camera.getzFar());

			editor.selectedObjectList.add(cube);
		}
	}

	public Vector3 getMousePos() {
		float width = Game.getInstance().getWidth();
		float height = Game.getInstance().getHeight();
		float aspect = width / height;
		float x = ((2f * Mouse.getX()) / Game.getInstance().getWidth() - 1f) * 10f * aspect;
		float y = ((2f * Mouse.getY()) / Game.getInstance().getHeight() - 1f) * 10f;

		x += editor.camera.position.getX();
		y += editor.camera.position.getY();

		return new Vector3(x, y, 0f);
	}

	// -- Listeners --
	@Override
	public void onMouseMoved(int amount_x, int amount_y) {
		if (selectedVertex != null) {
			Vector3 mousePos = getMousePos();
			mousePos.sub(selectedObject.position);
			selectedVertex.position.setX(mousePos.getX());
			selectedVertex.position.setY(mousePos.getY());

			setCornersPosition();
			selectedVertex.changed = true;
		}
		else if (click && selectedObject != null) {
			Vector3 mousePos = getMousePos();

			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				mousePos.sub(grabPos);
				Vector3 amount = new Vector3(amount_x * 0.05f, amount_y * 0.05f, 0);
				for (Vertex v : selectedObject.getShape().verticesList) {
					v.position.addX(amount.getX() * MathUtil.signum(v.position.getX()));
					v.position.addY(amount.getY() * MathUtil.signum(v.position.getY()));
					v.changed = true;
				}
			}
			else {
				Vector3 midPoint = new Vector3();
				int count = 0;
				for (Vertex v : selectedObject.getShape().verticesList) {
					midPoint.add(v.position);
					count++;
				}
				midPoint.scale(1f / count);
				selectedObject.position.add(midPoint);
				for (Vertex v : selectedObject.getShape().verticesList) {
					v.position.sub(midPoint);
					v.changed = true;
				}
				float result_x = mousePos.getX() + grabPos.getX();
				float result_y = mousePos.getY() + grabPos.getY();
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					result_x = MathUtil.round(result_x, 0);
					result_y = MathUtil.round(result_y, 0);
				}
				selectedObject.position.setX(result_x);
				selectedObject.position.setY(result_y);
			}

			setCornersPosition();
		}
	}

	@Override
	public void onMousePress(int button) {
		if (!Controls.isGrabbed() && button == 0) {
			click = true;
			Vector3 mousePos = getMousePos();

			if (selectedObject != null) {
				AABBRectangle rect = new AABBRectangle(mousePos, new Vector3());
				for (Vertex v : selectedObject.getShape().verticesList) {
					mousePos.setZ(v.position.getZ() + selectedObject.position.getZ());
					AABBRectangle vRect = new AABBRectangle(v.position.getAdd(selectedObject.position), new Vector3(
							0.25f));
					if (rect.collide(vRect)) {
						selectedVertex = v;
						return;
					}
				}
			}

			selectedObject = null;
			editor.selectedObjectList.clear();

			for (DynamicObject d : editor.dynamicList) {
				mousePos.setZ(d.position.getZ());
				if (d.getShape().pointInPolygon(d.position, mousePos)) {
					selectedObject = d;
					setCornersPosition();
					grabPos.set(d.position.getSub(mousePos));
					break;
				}
			}
		}
	}

	@Override
	public void onMouseRelease(int button) {
		if (button == 1) {
			Controls.grabMouse(!Controls.isGrabbed());
		}
		if (button == 0) {
			click = false;
			selectedVertex = null;
		}
	}

	@Override
	public void onKeyPress(int key) {
		if (key == Keyboard.KEY_Z) {
			upGoal = UPDOWN_SPEED;
		}
		else if (key == Keyboard.KEY_S) {
			downGoal = -UPDOWN_SPEED;
		}
		else if (key == Keyboard.KEY_Q) {
			leftGoal = -SIDEWAYS_SPEED;
		}
		else if (key == Keyboard.KEY_D) {
			rightGoal = SIDEWAYS_SPEED;
		}
		else if (key == Keyboard.KEY_NUMPAD8) {
			if (selectedVertex != null) {
				selectedVertex.uvY += 0.1f;
				selectedVertex.changed = true;
			}
		}
		else if (key == Keyboard.KEY_NUMPAD2) {
			if (selectedVertex != null) {
				selectedVertex.uvY -= 0.1f;
				selectedVertex.changed = true;
			}
		}
		else if (key == Keyboard.KEY_NUMPAD4) {
			if (selectedVertex != null) {
				selectedVertex.uvX += 0.1f;
				selectedVertex.changed = true;
			}
		}
		else if (key == Keyboard.KEY_NUMPAD6) {
			if (selectedVertex != null) {
				selectedVertex.uvX -= 0.1f;
				selectedVertex.changed = true;
			}
		}
		else if (key == Keyboard.KEY_SUBTRACT) {
			if (selectedObject != null) {
				selectedObject.position.addZ(+0.1f);
			}
		}
		else if (key == Keyboard.KEY_ADD) {
			if (selectedObject != null) {
				selectedObject.position.addZ(-0.1f);
			}
		}
	}

	@Override
	public void onKeyRelease(int key) {
		if (key == Keyboard.KEY_Z) {
			upGoal = 0f;
		}
		else if (key == Keyboard.KEY_S) {
			downGoal = 0f;
		}
		else if (key == Keyboard.KEY_Q) {
			leftGoal = 0;
		}
		else if (key == Keyboard.KEY_D) {
			rightGoal = 0;
		}
		else if (key == Keyboard.KEY_ESCAPE) {
			Game.end();
		}
	}

}
