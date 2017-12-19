package br.ufpe.simulator.math.integral;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;

public class DefaultBivariateIntegrator implements BivariateIntegrator {

	private static final int MAX_EVAL = 100000;
	private double relativeAccuracy;
	private double absoluteAccuracy;

	public DefaultBivariateIntegrator(double relativeAccuracy,
			double absoluteAccuracy) {
		super();
		this.relativeAccuracy = relativeAccuracy;
		this.absoluteAccuracy = absoluteAccuracy;
	}

	@Override
	public double integrate(final BivariateFunction f, double yMin,
			double yMax, final double xMin, final double xMax)
			throws TooManyEvaluationsException, MaxCountExceededException,
			MathIllegalArgumentException, NullArgumentException {

		UnivariateIntegrator yIntegrator = createTrapezoidalIntegrator();
		return yIntegrator.integrate(MAX_EVAL, new UnivariateFunction() {

			@Override
			public double value(final double y) {
				UnivariateIntegrator xIntegrator = createTrapezoidalIntegrator();
				return xIntegrator.integrate(MAX_EVAL,
						new UnivariateFunction() {

							@Override
							public double value(double x) {
								return f.value(x, y);
							}
						}, xMin, xMax);
			}
		}, yMin, yMax);

	}

	private TrapezoidIntegrator createTrapezoidalIntegrator() {
		return new TrapezoidIntegrator(relativeAccuracy, absoluteAccuracy, 3,
				64);
	}
}
