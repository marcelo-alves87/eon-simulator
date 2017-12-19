package br.ufpe.simulator.math;

public interface IsRandomVariable {

	public enum RandomVariableType {
		EXPONENTIALDISTRIBUTION
	}

	double sample();
}
