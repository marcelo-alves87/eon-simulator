package br.ufpe.simulator.utils;

import java.util.Random;

public class NumberGeneratorUtils {

	public static int generateInteger(int range) {
		Random random = new Random();
		return random.nextInt(range);
	}

	public static double generateDouble(double minValue, double maxValue) {
		Random r = new Random();
		return minValue + (maxValue - minValue) * r.nextDouble();
	}

	public static double random() {
		Random r = new Random();
		return r.nextDouble();
	}

}
