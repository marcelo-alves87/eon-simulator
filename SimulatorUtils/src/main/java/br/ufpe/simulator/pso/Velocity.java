package br.ufpe.simulator.pso;

import java.util.Arrays;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// bean class to represent velocity

public class Velocity {
	// store the Velocity in an array to accommodate multi-dimensional problem
	// space
	private double[] vel;

	public Velocity(double[] vel) {
		super();
		this.vel = vel;
	}

	public double[] getPos() {
		return vel;
	}

	public void setPos(double[] vel) {
		this.vel = vel;
	}

	@Override
	public String toString() {
		return "Velocity [vel=" + Arrays.toString(vel) + "]";
	}

}
