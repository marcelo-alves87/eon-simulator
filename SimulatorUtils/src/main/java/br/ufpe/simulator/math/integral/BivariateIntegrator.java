package br.ufpe.simulator.math.integral;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;

public interface BivariateIntegrator {

	double integrate(final BivariateFunction f, double yMin, double yMax,
			double xMin, double xMax) throws TooManyEvaluationsException,
			MaxCountExceededException, MathIllegalArgumentException,
			NullArgumentException;
}
