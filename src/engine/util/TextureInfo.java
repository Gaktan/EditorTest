package engine.util;

public final class TextureInfo {

	private int width;
	private int height;
	private String name;
	private int id;

	public TextureInfo(int id, int width, int height, String name) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.name = name;
	}

	@Override
	public String toString() {
		return "TextureInfo [id=" + id + ", name=" + name + ", width=" + width + ", height=" + height + "]";
	}

	// Getters
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
