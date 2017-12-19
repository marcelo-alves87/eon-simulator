package br.ufpe.simulator.pso;

public class ProblemRange {

	private double low;

	private double high;

	private double velocityLow;

	private double velocityHigh;

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getVelocityLow() {
		return velocityLow;
	}

	public void setVelocityLow(double velocityLow) {
		this.velocityLow = velocityLow;
	}

	public double getVelocityHigh() {
		return velocityHigh;
	}

	public void setVelocityHigh(double velocityHigh) {
		this.velocityHigh = velocityHigh;
	}

}
