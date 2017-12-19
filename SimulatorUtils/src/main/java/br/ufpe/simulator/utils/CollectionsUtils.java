package br.ufpe.simulator.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionsUtils {

	public static <T> T getLast(List<T> list) {
		T t = null;
		if (!list.isEmpty()) {
			t = list.get(list.size() - 1);
		}
		return t;
	}

	public static <T> T getFirst(List<T> list) {
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	public static <T> boolean isNullOrEmpty(Collection<T> collection) {
		return collection == null || collection.isEmpty();
	}

	public static <T> List<T> diff(List<T> list1, List<T> list2) {
		List<T> diffList = new ArrayList<T>();
		if (list1 != null && list2 != null) {
			for (T t : list1) {
				if (!list2.contains(t)) {
					diffList.add(t);
				}
			}
		}
		return diffList;
	}
}
