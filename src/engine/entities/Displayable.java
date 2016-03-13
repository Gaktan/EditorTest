package engine.entities;


/**
 * Highest interface of the engine. Allows you to update and render any object
 *
 * @author Gaktan
 */
public interface Displayable {

	/**
	 * Should be used to delete this object
	 */
	public void delete();

	/**
	 * Should be called upon destruction
	 */
	public void dispose();

	/**
	 * Used to render this object
	 *
	 * @param camera
	 */
	public void render();

	public boolean update(float dt);
}
