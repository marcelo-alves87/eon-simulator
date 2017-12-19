package br.ufpe.simulator.math;

import br.ufpe.simulator.math.IsRandomVariable.RandomVariableType;

public class RandomVariableFactory {

	public static IsRandomVariable createRandomVariable(
			RandomVariableType type, double mean) {
		IsRandomVariable randomVariable = null;
		if (type.equals(RandomVariableType.EXPONENTIALDISTRIBUTION)) {
			randomVariable = new CustomExponentialDistribution(mean);
		}
		return randomVariable;
	}
}
