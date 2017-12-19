package br.ufpe.simulatorkernel.domain;

public class Power {

	private double value;
	private double noise;

	public Power(IsPhysicalElement isPhysicalElement) {
		super();
		this.value = isPhysicalElement.getG();
		this.noise = isPhysicalElement.getLinearNoise();
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getNoise() {
		return noise;
	}

	public void setNoise(double noise) {
		this.noise = noise;
	}

	public double getOSNR() {
		return value / noise;
	}

}
