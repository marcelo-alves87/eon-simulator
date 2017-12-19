package br.ufpe.simulator.pso;

public class Particle {
	private double fitnessValue;
	private Velocity velocity;
	private Location location;

	public Particle() {
		super();
	}

	public Particle(double fitnessValue, Velocity velocity, Location location) {
		super();
		this.fitnessValue = fitnessValue;
		this.velocity = velocity;
		this.location = location;
	}

	public Velocity getVelocity() {
		return velocity;
	}

	public void setVelocity(Velocity velocity) {
		this.velocity = velocity;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public double getFitnessValue(IProblemSet iProblemSet) {
		fitnessValue = iProblemSet.evaluate(location);
		return fitnessValue;
	}
}
