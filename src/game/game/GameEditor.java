package game.game;

import java.awt.Canvas;
import java.util.Comparator;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import engine.entities.BitMapFont;
import engine.entities.Camera;
import engine.entities.DisplayableList;
import engine.entities.DisplayableText;
import engine.entities.DynamicObject;
import engine.entities.Light;
import engine.game.Controls;
import engine.game.Fps;
import engine.game.FrameBuffer;
import engine.game.Game;
import engine.shapes.DynamicQuad;
import engine.shapes.ShaderProgram;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeQuadTexture;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.TextureUtil;
import engine.util.Vector3;

public class GameEditor extends Game {

	protected static final float MAX_DELTA = 40.f;

	public static boolean SKIP_MENU;
	public static boolean DEBUG;

	protected BitMapFont font;
	protected Fps fps;
	protected DisplayableText textFps;

	// Programs
	private ShaderProgram programTexture;
	private ShaderProgram programColor;
	private ShaderProgram programTexCameraInstanced;
	private ShaderProgram programFrameBuffer;
	private ShaderProgram program1Dtexture;
	private ShaderProgram programLight;
	private ShaderProgram programOcclusion;

	public DisplayableList<DynamicObject> selectedObjectList;
	public DisplayableList<DynamicObject> dynamicList;
	public Camera camera;
	public Controller controller;
	public Light light;

	private FrameBuffer colorBuffer;
	private FrameBuffer occlusionBuffer;
	private FrameBuffer shadowMap;

	public GameEditor(int width, int height, boolean fullScreen, Canvas parent) {
		super(width, height, fullScreen, parent);
	}

	@Override
	public void init() {
		if (!DEBUG) {
			limitFPS = 60;
			Display.setVSyncEnabled(true);
		}

		// Enable Depth Testing
		// GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		// fps
		fps = new Fps();

		// Shaders
		programTexture = new ShaderProgram("texture");
		programColor = new ShaderProgram("color");
		programTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture", "texture_camera_instanced");
		programFrameBuffer = new ShaderProgram("frameBuffer", "texture", "frameBuffer");
		program1Dtexture = new ShaderProgram("texture", "1Dtexture", "1Dtexture");
		programLight = new ShaderProgram("texture", "light", "light");
		programOcclusion = new ShaderProgram("texture", "occlusion", "occlusion");

		// Set program uniforms
		program1Dtexture.setUniform(Uniform.model, Matrix4.createModelMatrix(new Vector3()));
		program1Dtexture.setUniform(Uniform.color, new Vector3(1f));

		// INIT HERE
		camera = new Camera(45f, getWidth() / (float) getHeight(), -0.2f, 10.5f) {
			@Override
			public void setProjection() {
				float v = 10f * aspect;
				projection = Matrix4.createOrthoProjectionMatrix(-v, v, 10f, -10f, zNear, zFar);
			}
		};
		camera.position.setZ(camera.getzFar());
		controller = new Controller(this);

		dynamicList = new DisplayableList<DynamicObject>();
		selectedObjectList = new DisplayableList<DynamicObject>();

		for (int i = 0; i < 100; i++) {
			DynamicObject a = new DynamicObject(new DynamicQuad("wall.png"));
			a.position.set(MathUtil.randomCoord(new Vector3(-10, -10, 0f), new Vector3(10, 10, 10)));
			dynamicList.add(a);
		}

		font = new BitMapFont(new ShapeInstancedSprite("scumm_font.png", 8, 11));

		// Fps
		fps = new Fps();
		textFps = font.createString(new Vector3(-1f, .9f, 0), "", 1.1f);

		light = new Light(new Vector3(0f, 0f, camera.getzNear() + 1f), 1f, new Vector3(1.0f, 0.8f, 0.4f));
		dynamicList.add(light);

		// Buffers
		colorBuffer = new FrameBuffer(programTexture, programFrameBuffer, 400, 300);
		occlusionBuffer = new FrameBuffer(programOcclusion, programFrameBuffer, light.resolution, light.resolution);
		shadowMap = new FrameBuffer(program1Dtexture, programFrameBuffer, light.resolution, 1);
		light.setTexture(shadowMap.getTexture());
	}

	@Override
	public void render() {
		// occlusion
		Camera cam = new Camera(45f, 1f, +0.2f, 10.5f) {
			@Override
			public void setProjection() {
				float v = 10f * light.getRadius();
				projection = Matrix4.createOrthoProjectionMatrix(-v, v, v, -v, zNear, -zFar);
			}
		};
		cam.position.set(light.position);
		cam.position.setZ(cam.getzNear());
		cam.apply();

		occlusionBuffer.bind();
		occlusionBuffer.clear();

		ShaderProgram program = ShaderProgram.getCurrentProgram();
		program.setUniform(Uniform.projection, cam.getProjection());
		program.setUniform(Uniform.view, cam.getMatrixView());
		program.setUniform(Uniform.zfar, cam.getzFar());
		program.setUniform("u_lightZ", light.position.getZ());

		dynamicList.render();

		// occlusionBuffer.render();
		FrameBuffer.unbind();

		// Shadows!
		cam = new Camera(45f, 1f, -0.2f, 10.1f) {
			@Override
			public void setProjection() {
				float v = 0.5f;
				projection = Matrix4.createOrthoProjectionMatrix(v, -v, -v, v, zNear, zFar);
			}
		};
		cam.position.set(0f);
		cam.position.setZ(-.2f);
		cam.apply();

		shadowMap.bind();
		shadowMap.clear();

		program = ShaderProgram.getCurrentProgram();
		program.setUniform(Uniform.projection, cam.getProjection());
		program.setUniform(Uniform.view, cam.getMatrixView());
		// program.setUniform(Uniform.zfar, cam.getzFar());

		ShapeQuadTexture shape = new ShapeQuadTexture(occlusionBuffer.getTexture());
		ShaderProgram.getCurrentProgram().setUniform("u_resolution", light.resolution, light.resolution);

		shape.preRender();
		shape.render();
		shape.postRender();
		shape.dispose();

		// shadowMap.render();

		GL30.glBindVertexArray(0);
		FrameBuffer.unbind();

		// Normal drawing
		camera.apply();
		setProgramsUniform();

		colorBuffer.bind();
		colorBuffer.clear();

		dynamicList.render();
		light.realRender();

		if (selectedObjectList != null) {
			selectedObjectList.render();
		}

		colorBuffer.render();
		FrameBuffer.unbind();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		ShaderProgram.setCurrentProgram(programTexCameraInstanced);
		textFps.render();
	}

	@Override
	public void resized() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		if (camera != null) {
			float aspect = (float) getWidth() / (float) getHeight();
			camera.setAspect(aspect);
		}
	}

	@Override
	public void update(float dt) {
		if (dt > MAX_DELTA) {
			dt = MAX_DELTA;
		}

		long l_fps = fps.update();

		dynamicList.sort(new Comparator<DynamicObject>() {
			@Override
			public int compare(DynamicObject o1, DynamicObject o2) {
				return -Float.compare(o1.position.getZ(), o2.position.getZ());
			}
		});
		dynamicList.update(dt);

		Controls.update();
		controller.update(dt);

		textFps.setText("fps : " + l_fps);
		textFps.update(dt);

		if (controller.selectedObject != null) {
			selectedObjectList.update(dt);
		}
	}

	public static GameEditor getInstance() {
		return (GameEditor) instance;
	}

	@Override
	public void dispose() {
		controller.dispose();
		dynamicList.dispose();
		selectedObjectList.dispose();

		font.dispose();

		// Buffers
		colorBuffer.dispose();
		shadowMap.dispose();
		occlusionBuffer.dispose();

		// Texture
		TextureUtil.deleteTextures();

		// Shaders
		program1Dtexture.dispose();
		programColor.dispose();
		programFrameBuffer.dispose();
		programLight.dispose();
		programOcclusion.dispose();
		programTexCameraInstanced.dispose();
		programTexture.dispose();
	}

	protected void setProgramUniform(ShaderProgram program) {
		program.bind();
		program.setUniform(Uniform.projection, camera.getProjection());
		program.setUniform(Uniform.view, camera.getMatrixView());
		program.setUniform(Uniform.zfar, camera.getzFar());
	}

	protected void setProgramsUniform() {
		setProgramUniform(programTexture);
		setProgramUniform(programColor);
		setProgramUniform(programTexCameraInstanced);
		setProgramUniform(programFrameBuffer);
		setProgramUniform(programLight);

		ShaderProgram.unbind();
	}

}
