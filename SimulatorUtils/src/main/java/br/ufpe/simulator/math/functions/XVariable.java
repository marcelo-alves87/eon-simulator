package br.ufpe.simulator.math.functions;

import org.apache.commons.math3.analysis.TrivariateFunction;

public class XVariable implements TrivariateFunction {

	private double ns; // numberOfSpans;
	private double beta2;
	private double beta3;
	private double ls;

	public XVariable(double ns, double beta2, double beta3, double ls) {
		super();
		this.ns = ns;
		this.beta2 = beta2;
		this.beta3 = beta3;
		this.ls = ls;
	}

	@Override
	public double value(double f1, double f2, double f) {
		return 1;
	}

}
