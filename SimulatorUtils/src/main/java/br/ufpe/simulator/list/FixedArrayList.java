package br.ufpe.simulator.list;

import java.util.ArrayList;
import java.util.Collections;

public class FixedArrayList<T extends Comparable<T>> extends ArrayList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxSize;

	public FixedArrayList(int maxSize) {
		super();
		this.maxSize = maxSize;
	}

	@Override
	public boolean add(T e) {
		boolean isAdd = super.add(e);
		if (size() > maxSize) {
			Collections.sort(this);
			remove(size() - 1);
		}
		return isAdd;
	}

}
