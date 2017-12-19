package br.ufpe.simulator.math.functions;

import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.complex.Complex;

public class RoVariable implements TrivariateFunction {

	private double alfa; // field loss coefficient
	private double ls; // span length
	private double beta2; // dispersion coefficient
	private double beta3; // dispersion slope
	private Leff leff;

	public RoVariable(double alfa, double ls, double beta2, double beta3) {
		super();
		this.alfa = alfa;
		this.ls = ls;
		this.beta2 = beta2;
		this.beta3 = beta3;
		this.leff = new Leff();
	}

	@Override
	public double value(double f1, double f2, double f) {
		Complex complex1 = new Complex(1, 0);
		Complex complex21 = new Complex(Math.exp(-2 * alfa * ls), 0);
		Complex complex22 = new Complex(Math.cos(4 * Math.pow(Math.PI, 2)
				* (f1 - f) * (f2 - f) * (beta2 + Math.PI * beta3 * (f1 + f2))
				* ls), Math.sin(4 * Math.pow(Math.PI, 2) * (f1 - f) * (f2 - f)
				* (beta2 + Math.PI * beta3 * (f1 + f2)) * ls));
		Complex complex2 = complex21.multiply(complex22);
		Complex complex3 = complex1.subtract(complex2);

		Complex complex4 = new Complex(2 * alfa, -4 * Math.pow(Math.PI, 2)
				* (f1 - f) * (f2 - f) * (beta2 + Math.PI * beta3 * (f1 + f2)));

		Complex complex5 = complex3.divide(complex4);
		return Math.pow(complex5.abs(), 2) / Math.pow(leff.value(alfa, ls), 2);
	}
}
