package br.ufpe.simulator.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class RaisedCosine implements UnivariateFunction {

	private double ts;
	private double rollOff;
	private double offSet;

	public RaisedCosine(double ts, double rollOff, double offSet) {
		super();
		this.ts = ts;
		this.rollOff = rollOff;
		this.offSet = offSet;
	}

	@Override
	public double value(double x) {
		if (ts == 0) {
			return 0;
		} else {
			x = x - offSet;
			double value = 0;
			if (0 <= Math.abs(x) && Math.abs(x) < ((1 - rollOff) / (2 * ts))) {
				value = ts;
			} else if (((1 - rollOff) / (2 * ts)) <= Math.abs(x)
					&& Math.abs(x) < ((1 + rollOff) / (2 * ts))) {
				value = (ts / 2)
						* (1 + Math.cos(((Math.PI * ts / rollOff) * (Math
								.abs(x) - ((1 - rollOff) / (2 * ts))))));
			} else {
				value = 0;
			}
			return value;
		}
	}

	public double getTs() {
		return ts;
	}

	public void setTs(double ts) {
		this.ts = ts;
	}

	public double getRollOff() {
		return rollOff;
	}

	public void setRollOff(double rollOff) {
		this.rollOff = rollOff;
	}

	public double getOffSet() {
		return offSet;
	}

	public void setOffSet(double offSet) {
		this.offSet = offSet;
	}

}
