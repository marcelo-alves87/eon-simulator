package br.ufpe.simulator.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StringUtil {

	public static List<String> split(String string, String regex) {
		return string != null && regex != null ? Arrays.asList(string
				.split(regex)) : null;
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.equals("");
	}

	public static String generateString() {
		return UUID.randomUUID().toString();
	}
}
