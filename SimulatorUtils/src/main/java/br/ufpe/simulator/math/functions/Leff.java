package br.ufpe.simulator.math.functions;

import org.apache.commons.math3.analysis.BivariateFunction;

public class Leff implements BivariateFunction {
	
	@Override
	public double value(double alfa, double ls) {
		return ((1 - Math.exp(-2 * alfa * ls)) / (2 * alfa));
	}
}
