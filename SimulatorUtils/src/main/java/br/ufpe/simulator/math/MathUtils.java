package br.ufpe.simulator.math;

public class MathUtils {

	public static boolean isOdd(int i) {
		return i % 2 != 0;
	}

	public static double convertdBmToLinear(double powerdBm) {
		return Math.pow(10, powerdBm / 10) / 1000;
	}

	public static double convertToAlfaLinear(double alfadB) {
		return alfadB / (10 * Math.log10(Math.E));
	}

	public static double converdBToLinear(double x) {
		return Math.pow(10, x / 10);
	}

	public static double convertLinearTodB(double x) {
		return 10 * Math.log10(x);
	}

	public static double convertLinearTodBm(double x) {
		return 10 * Math.log10(x) + 30;
	}

	public static double log2(double x) {
		return Math.log10(x) / Math.log10(2);
	}

	public static double soma(double x) {
		if (x == 1) {
			return 1;
		} else {
			return x + soma(x - 1);
		}
	}

	public static double raiz(double x, double y) {
		return Math.pow(x, 1 / y);
	}

	public static int roudAndCompare(double x, double y) {
		return Double.compare(Math.round(x), Math.round(y));
	}

	public static double convertNanoToSeconds(double totalTime) {
		return (double) totalTime / 1000000000;
	}

	public static double soma(double x, int n) {
		double value = 0;
		for (int i = 0; i < n; i++) {
			value += Math.pow(x, i + 1);
		}
		return value;
	}

}
