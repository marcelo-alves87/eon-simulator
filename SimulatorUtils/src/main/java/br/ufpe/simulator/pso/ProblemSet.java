package br.ufpe.simulator.pso;

import java.util.List;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// this is the problem to be solved
// to find an x and a y that minimize the function below:
// f(x, y) = (2.8125 - x + x * y^4)^2 + (2.25 - x + x * y^2)^2 + (1.5 - x + x*y)^2
// where 1 <= x <= 4, and -1 <= y <= 1

// you can modify the function depends on your needs
// if your problem space is greater than 2-dimensional space
// you need to introduce a new variable (other than x and y)

public class ProblemSet implements IProblemSet {

	public double evaluate(Location location) {
		double result = 0;
		double x = location.getLoc()[0]; // the "x" part of the location
		double y = location.getLoc()[1]; // the "y" part of the location

		result = Math.pow(2.8125 - x + x * Math.pow(y, 4), 2) + Math.pow(2.25 - x + x * Math.pow(y, 2), 2)
				+ Math.pow(1.5 - x + x * y, 2);

		return result;
	}

	@Override
	public int getProblemDimension() {
		return 2;
	}

	@Override
	public List<Double[]> getLinkCosts() {
		return null;
	}
}