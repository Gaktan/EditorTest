package engine.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Acts as an ArrayList for Displayables
 *
 * @author Gaktan
 */
public class DisplayableList<E extends Displayable> implements Displayable, Iterable<E> {

	public List<E> list;
	private List<E> toBeAdded;
	private List<E> toBeDeleted;
	private boolean delete = false;

	public DisplayableList() {
		list = new ArrayList<E>();
		toBeAdded = new ArrayList<E>();
		toBeDeleted = new ArrayList<E>();
	}

	public DisplayableList(DisplayableList<E> otherList) {
		this();
		addAll(otherList);
	}

	/**
	 * Adds a displayable to the list
	 *
	 * @param d
	 *            Displayable to add
	 */
	public void add(E d) {
		toBeAdded.add(d);
	}

	public void addAll(DisplayableList<E> otherList) {
		toBeAdded.addAll(otherList.list);
	}

	public void deleteAll() {
		toBeDeleted.addAll(list);
	}

	@Override
	public void delete() {
		delete = true;
	}

	public void clear() {
		dispose();
		list.clear();
	}

	@Override
	public void dispose() {
		for (E d : list) {
			d.dispose();
		}
	}

	public E get(int i) {
		return list.get(i);
	}

	public int indexOf(E d) {
		return list.indexOf(d);
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	/**
	 * Removes a displayable from the list
	 *
	 * @param d
	 *            Displayable to remove
	 */
	public void remove(E d) {
		toBeDeleted.add(d);
	}

	@Override
	public void render() {
		for (Displayable d : list) {
			d.render();
		}
	}

	@Override
	public boolean update(float dt) {
		for (E d : toBeAdded) {
			list.add(d);
		}
		toBeAdded.clear();

		for (E d : list) {
			boolean b = d.update(dt);
			if (!b)
				remove(d);
		}

		for (E d : toBeDeleted) {
			d.dispose();
			list.remove(d);
		}
		toBeDeleted.clear();

		return !delete;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void sort(Comparator<E> comparator) {
		list.sort(comparator);
	}
}
