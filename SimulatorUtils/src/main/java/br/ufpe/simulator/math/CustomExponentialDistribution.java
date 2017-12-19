package br.ufpe.simulator.math;

import br.ufpe.simulator.utils.NumberGeneratorUtils;

public class CustomExponentialDistribution implements IsRandomVariable {

	private double mean;

	public CustomExponentialDistribution(double mean) {
		this.mean = mean;
	}

	@Override
	public double sample() {
		return -mean * Math.log10(NumberGeneratorUtils.generateDouble(0, 1));
	}
}
