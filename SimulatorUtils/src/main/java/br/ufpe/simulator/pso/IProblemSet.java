package br.ufpe.simulator.pso;

import java.util.List;

public interface IProblemSet {

	public static final double ERR_TOLERANCE = 1E-20;

	List<ProblemRange> getProblemRanges();

	int getProblemDimension();

	double evaluate(Location location);
	
	List<Double[]> getLinkCosts();
}
