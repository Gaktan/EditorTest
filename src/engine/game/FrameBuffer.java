package engine.game;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import engine.shapes.ShaderProgram;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.ShapeQuadTexture;
import engine.util.Matrix4;
import engine.util.TextureInfo;
import engine.util.Vector3;

public class FrameBuffer {

	public interface RenderLast {
		public void renderLast();
	}

	protected int renderBuffer;
	protected int frameBuffer;
	protected ShapeQuadTexture screenShape;
	protected ShaderProgram programRender;
	protected ShaderProgram programBuffer;
	protected ArrayList<RenderLast> renderLastList;

	protected TextureInfo texture;

	public FrameBuffer(ShaderProgram programRender, ShaderProgram programBuffer, int width, int height) {
		this.programRender = programRender;
		this.programBuffer = programBuffer;
		renderLastList = new ArrayList<RenderLast>();
		texture = new TextureInfo(-1, width, height, "");

		init();
	}

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, texture.getWidth(), texture.getHeight());

		ShaderProgram.setCurrentProgram(programRender);
	}

	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0f, 0f, 0f, 0f);
	}

	public void dispose() {
		GL11.glDeleteTextures(texture.getId());
		GL30.glDeleteRenderbuffers(renderBuffer);
		GL30.glDeleteFramebuffers(frameBuffer);

		screenShape.dispose();
	}

	public void init() {
		// Framebuffer
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		// Create a color attachment texture
		int attachment_type = GL11.GL_RGBA;

		// Generate texture ID and load texture data
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, attachment_type, texture.getWidth(), texture.getHeight(), 0,
				attachment_type, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureID, 0);

		renderBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, texture.getWidth(),
				texture.getHeight());

		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
				renderBuffer);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("ERROR! Framebuffer is not complete!");
			Game.end();
			return;
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		texture.setId(textureID);
		screenShape = new ShapeQuadTexture(texture);
	}

	public void render() {
		ShaderProgram.setCurrentProgram(programBuffer);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, Game.getInstance().getWidth(), Game.getInstance().getHeight());

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		screenShape.preRender();

		ShaderProgram.getCurrentProgram().setUniform(Uniform.color, new Vector3(1f));
		Matrix4 model = Matrix4.createIdentityMatrix();
		model.scale(new Vector3(2f));
		ShaderProgram.getCurrentProgram().setUniform(Uniform.model, model);
		ShaderProgram.getCurrentProgram().setUniform(Uniform.spriteNumber, -1f);

		screenShape.render();
		screenShape.postRender();

		for (RenderLast r : renderLastList) {
			r.renderLast();
		}
		renderLastList.clear();
		ShaderProgram.setCurrentProgram(null);
	}

	public static void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		ShaderProgram.setCurrentProgram(null);
	}

	public TextureInfo getTexture() {
		return texture;
	}

	public void addRenderLast(RenderLast renderLast) {
		renderLastList.add(renderLast);
	}
}
