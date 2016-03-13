package engine.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 *
 * TextureUtil is a class used to hold static methods used to manipulate
 * textures
 *
 * @author Gaktan
 *
 */
public final class TextureUtil {

	public static TextureInfo MISSING_TEXTURE;
	public static TextureInfo NO_TEXTURE;

	public static HashMap<String, TextureInfo> textureMap;

	static {
		textureMap = new HashMap<String, TextureInfo>();
		setDefaultTextures();
		textureMap.put("", MISSING_TEXTURE);
		textureMap.put("none", NO_TEXTURE);
	}

	/**
	 * Loads a texture from a file
	 *
	 * @param fileName
	 *            name of the texture
	 * @return Texture
	 */
	public static TextureInfo loadTexture(String fileName) {
		TextureInfo info = getTexture(fileName);

		if (info != null) {
			return info;
		}

		int textureID = 0;

		/*
		// Wrap methods
		// GL11.GL_REPEAT
		// GL14.GL_MIRRORED_REPEAT
		// GL12.GL_CLAMP_TO_EDGE
		// L13.GL_CLAMP_TO_BORDER
		int wrap = GL11.GL_REPEAT;
		*/

		// Filter methods
		// GL11.GL_LINEAR
		// GL11.GL_LINEAR_MIPMAP_NEAREST
		// GL11.GL_LINEAR_MIPMAP_LINEAR
		// GL11.GL_NEAREST
		// GL11.GL_NEAREST_MIPMAP_NEAREST
		// GL11.GL_NEAREST_MIPMAP_LINEAR
		int filter = GL11.GL_NEAREST;

		Texture t;
		try {
			FileInputStream fis = new FileInputStream("res/images/" + fileName);
			// TODO: make my own, because this is garbage
			t = TextureLoader.getTexture("PNG", fis, filter);
			fis.close();

			textureID = t.getTextureID();

		} catch (FileNotFoundException e) {
			System.err.println("Texture \"" + fileName + "\" missing.");
			return MISSING_TEXTURE;
		} catch (IOException e) {
			e.printStackTrace();
			return MISSING_TEXTURE;
		} finally {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}

		TextureInfo newTexture = new TextureInfo(textureID, t.getImageWidth(), t.getImageHeight(), fileName);
		textureMap.put(fileName, newTexture);
		return newTexture;
	}

	public static void setDefaultTextures() {
		// -- Missing texture
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		byte[] magenta = new byte[] { (byte) 255, 0, (byte) 255 };
		byte[] black = new byte[] { 0, 0, 0 };
		byte[] transparent = new byte[] { 0, 0, 0, 0 };

		ByteBuffer bb = BufferUtils.createByteBuffer(3 * 4 * 4);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((j + i) % 2 == 0) {
					bb.put(magenta);
				}
				else {
					bb.put(black);
				}
			}
		}
		bb.flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 4, 4, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, bb);
		MISSING_TEXTURE = new TextureInfo(textureID, 4, 4, "missing");

		// -- No Texture
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		ByteBuffer bb2 = BufferUtils.createByteBuffer(4 * 1 * 1);
		bb2.put(transparent);
		bb2.flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb2);
		NO_TEXTURE = new TextureInfo(textureID, 1, 1, "none");

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void deleteTextures() {
		for (Entry<String, TextureInfo> e : textureMap.entrySet()) {
			TextureInfo textureInfo = e.getValue();
			GL11.glDeleteTextures(textureInfo.getId());
		}
		textureMap.clear();
	}

	public static TextureInfo getTexture(String textureName) {
		TextureInfo info = textureMap.get(textureName);
		return info;
	}

}
