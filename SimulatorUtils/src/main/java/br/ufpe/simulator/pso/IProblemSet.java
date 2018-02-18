package br.ufpe.simulator.pso;

import java.util.List;

public interface IProblemSet {

	public static final double LOC_LOW = 0;
	public static final double LOC_HIGH = 1;
	public static final double VEL_LOW = -1;
	public static final double VEL_HIGH = 1;

	public static final double ERR_TOLERANCE = 1E-20; // the smaller the
														// tolerance, the more
														// accurate the result,
														// but the number of
														// iteration is
														// increased
	
	int getProblemDimension();

	double evaluate(Location location);
	
	List<Double[]> getLinkCosts();
}
