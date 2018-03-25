package br.ufpe.simulator.pso;

import java.util.Arrays;

/* author: gandhi - gandhi.mtm [at] gmail [dot] com - Depok, Indonesia */

// bean class to represent location

public class Location {
	// store the Location in an array to accommodate multi-dimensional problem space
	private double[] loc;

	public Location(double[] loc) {
		super();
		this.loc = loc;
	}

	public double[] getLoc() {
		return loc;
	}

	public void setLoc(double[] loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		return "Location [loc=" + Arrays.toString(loc) + "]";
	}
	
}
