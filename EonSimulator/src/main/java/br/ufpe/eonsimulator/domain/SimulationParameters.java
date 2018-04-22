package br.ufpe.eonsimulator.domain;

public class SimulationParameters {

	private double connectionCurrentArrivalRate;
	private double connectionDeathRate;
	private double connectionMinArrival;
	private double connectionMaxArrival;
	private double connectionMinBitRate;
	private double connectionMaxBitRate;
	private int numberGraphPoints;
	private boolean isConnectionExpandible;
	private double connectionSlotWidth;
	private int geneticAlgorithmK;

	public double getConnectionDeathRate() {
		return connectionDeathRate;
	}

	public void setConnectionDeathRate(double connectionDeathRate) {
		this.connectionDeathRate = connectionDeathRate;
	}

	public double getConnectionMinArrival() {
		return connectionMinArrival;
	}

	public void setConnectionMinArrival(double connectionMinArrival) {
		this.connectionMinArrival = connectionMinArrival;
		this.connectionCurrentArrivalRate = connectionMinArrival;
	}

	public int getNumberGraphPoints() {
		return numberGraphPoints;
	}

	public void setNumberGraphPoints(int numberGraphPoints) {
		this.numberGraphPoints = numberGraphPoints;
	}

	public boolean isConnectionExpandible() {
		return isConnectionExpandible;
	}

	public void setConnectionExpandible(boolean isConnectionExpandible) {
		this.isConnectionExpandible = isConnectionExpandible;
	}

	public double getConnectionMinBitRate() {
		return connectionMinBitRate;
	}

	public double getConnectionMaxArrival() {
		return connectionMaxArrival;
	}

	public void setConnectionMaxArrival(double connectionMaxArrival) {
		this.connectionMaxArrival = connectionMaxArrival;
	}

	public double getConnectionCurrentArrivalRate() {
		return connectionCurrentArrivalRate;
	}

	public boolean isMaxArrivalRateReached() {
		return connectionCurrentArrivalRate >= connectionMaxArrival;
	}

	public void incrementArrivalRate() {
		connectionCurrentArrivalRate += (connectionMaxArrival - connectionMinArrival)
				/ (numberGraphPoints - 1);
	}

	public void setConnectionSlotWidth(double connectionSlotWidth) {
		this.connectionSlotWidth = connectionSlotWidth;

	}

	public double getConnectionSlotWidth() {
		return connectionSlotWidth;
	}

	public double getConnectionMaxBitRate() {
		return connectionMaxBitRate;
	}

	public void setConnectionMaxBitRate(double connectionMaxBitRate) {
		this.connectionMaxBitRate = connectionMaxBitRate;
	}

	public void setConnectionMinBitRate(double connectionMinBitRate) {
		this.connectionMinBitRate = connectionMinBitRate;
	}

	public int getMaxSlotExpandUnits() {
		return (int) Math.ceil(this.connectionMaxBitRate
				/ this.connectionSlotWidth);
	}

	public void clearArrivalRate() {
		connectionCurrentArrivalRate = connectionMinArrival;

	}

	public int getGeneticAlgorithmK() {
		return geneticAlgorithmK;
	}

	public void setGeneticAlgorithmK(int geneticAlgorithmK) {
		this.geneticAlgorithmK = geneticAlgorithmK;
	}

}