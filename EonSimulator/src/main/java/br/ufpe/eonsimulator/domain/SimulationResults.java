package br.ufpe.eonsimulator.domain;

import java.util.HashMap;
import java.util.Map;

public class SimulationResults {

	public class SimulationRequestCount {
		private long numberOfRequest;
		private long numberOfBlockedRequest;

		public void incrementNumberOfRequest() {
			this.numberOfRequest++;
		}

		public void incrementNumberOfBlockedRequest() {
			this.numberOfBlockedRequest++;
		}

		public double getBlockingProbability() {
			return (double) this.numberOfBlockedRequest / this.numberOfRequest;
		}

		public long getNumberOfRequest() {
			return numberOfRequest;
		}
	}

	private long numberOfBlockedRequests;
	private long numberOfNetworkBlockedRequests;
	private long numberOfRequests;
	private long numberOfPhysicalBlocking;
	private long numberOfRoutes;
	private long numberOfPhysicalValidRoutes;
	private Map<Double, SimulationRequestCount> bitRateBlockingProbability;

	public SimulationResults() {
		super();
		this.bitRateBlockingProbability = new HashMap<Double, SimulationRequestCount>();
	}

	public void clear() {
		numberOfBlockedRequests = 0;
		numberOfNetworkBlockedRequests = 0;
		numberOfRequests = 0;
		numberOfPhysicalBlocking = 0;
		numberOfRoutes = 0;
		numberOfPhysicalValidRoutes = 0;
	}

	public void incrementNumberOfBitRateBlockedRequest(double bitRate) {
		bitRateBlockingProbability.get(bitRate).incrementNumberOfBlockedRequest();
	}

	public void incrementNumberOfRequest(double bitRate) {
		createBitRateIfNotExists(bitRate);
		bitRateBlockingProbability.get(bitRate).incrementNumberOfRequest();
	}

	private void createBitRateIfNotExists(double bitRate) {
		if (!bitRateBlockingProbability.containsKey(bitRate)) {
			bitRateBlockingProbability.put(bitRate, new SimulationRequestCount());
		}
	}

	public void incrementNumberOfNetworkBlockedRequests() {
		this.numberOfNetworkBlockedRequests++;
		this.numberOfBlockedRequests++;
	}

	public double getValidPhysicalRoutesRate() {
		return (double) numberOfPhysicalValidRoutes / numberOfRoutes;
	}

	public void incrementNumberOfPhysicalValidRoutes(int validRoutes) {
		this.numberOfPhysicalValidRoutes += validRoutes;
		this.numberOfRoutes++;
	}

	public long getNumberOfRequests() {
		return numberOfRequests;
	}

	public void incrementNumberOfRequests() {
		this.numberOfRequests++;
	}

	public void incrementNumberOfPhysicalBlocking() {
		this.numberOfPhysicalBlocking++;
		this.numberOfBlockedRequests++;
	}

	public double getNetworkBlockingProbability() {
		return (double) numberOfNetworkBlockedRequests / numberOfRequests;
	}

	public double getPhysicalBlockingProbability() {
		return (double) numberOfPhysicalBlocking / numberOfRequests;
	}

	public double getBlockingProbability() {
		return (double) numberOfBlockedRequests / numberOfRequests;
	}

	public Map<Double, SimulationRequestCount> getBitRateBlockingProbability() {
		return bitRateBlockingProbability;
	}

	public void incrementNumberOfBlockedRequests() {
		this.numberOfBlockedRequests++;
	}

	public void setNumberOfRequests(long numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}
}
