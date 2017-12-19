package br.ufpe.simulator.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class Log2 implements UnivariateFunction {

	public static final Log2 INSTANCE = new Log2();
	
	@Override
	public double value(double x) {
		return Math.log10(x) / Math.log10(2);
	}

}
