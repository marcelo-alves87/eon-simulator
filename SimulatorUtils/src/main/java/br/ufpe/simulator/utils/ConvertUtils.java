package br.ufpe.simulator.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConvertUtils {

	public static String convertToString(Integer integer) {
		return integer != null ? Integer.toString(integer) : null;
	}

	public static int convertToInteger(String string) {
		return string != null ? Integer.parseInt(string.trim()) : 0;
	}

	public static double convertToDouble(String string) {
		return string != null ? Double.parseDouble(string.trim()) : 0;
	}

	public static boolean convertToBoolean(String string) {
		return string != null
				&& (string.trim().equalsIgnoreCase("true") || string.trim()
						.equalsIgnoreCase("1")) ? true : false;
	}

	public static String convertToLocaleString(double d) {
		return Double.toString(d).replace('.', ',');
	}

	public static String convertToString(double d) {
		return Double.toString(d);
	}

	public static String convertToString(int i) {
		return Integer.toString(i);
	}

	public static int convertToInteger(Double d) {
		return d != null ? d.intValue() : 0;
	}

	public static List<Double> convertToDouble(List<String> strings) {
		List<Double> doubles = new ArrayList<Double>();
		if (strings != null) {
			for (String string : strings) {
				doubles.add(convertToDouble(string));
			}
		}
		return doubles;
	}

	public static String convertToString(Object object) {
		return object != null ? object.toString() : null;
	}

	public static <T> List<T> convertToList(Iterator<T> iterator) {
		List<T> copy = new ArrayList<T>();
		while (iterator.hasNext()) {
			copy.add(iterator.next());
		}
		return copy;
	}
}
