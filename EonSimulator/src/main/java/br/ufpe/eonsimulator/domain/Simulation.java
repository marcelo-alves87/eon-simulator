package br.ufpe.eonsimulator.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.costFunctions.IsCostFunction;
import br.ufpe.eonsimulator.costFunctions.IsLinkCostFunction;
import br.ufpe.eonsimulator.costFunctions.output.IsOutputLinkCostFunction;
import br.ufpe.eonsimulator.modulation.IsModulationFormat;
import br.ufpe.eonsimulator.routing.IsRoutingAlgorithm;
import br.ufpe.eonsimulator.rsa.IsRSAAlgorithm;
import br.ufpe.eonsimulator.spectrumAssignment.IsSpectrumAssignmentAlgorithm;
import br.ufpe.eonsimulator.trafficGenerators.TrafficGenerator;
import br.ufpe.simulator.exceptions.EonSimulatorExceptions;
import br.ufpe.simulator.messages.MessageUtils;

public class Simulation {

	public enum SimulationResultsType {
		BITRATE, ERLANG, LINKSCOST, SIMPLEERLANG, SIMPLEERLANG_LINKCOST
	}

	private static final String SIMULATION_REMOVE_CONNECTION_INFO = "simulation.remove.connection.info";

	private static Logger logger = Logger.getLogger(Simulation.class);

	private SimulationParameters parameters;
	private Topology topology;
	private IsRoutingAlgorithm isRoutingAlgorithm;
	private IsSpectrumAssignmentAlgorithm isAssignmentAlgorithm;
	private IsCostFunction costFunction;
	private TrafficGenerator trafficGenerator;
	private double maxSimulationTime;
	private List<Connection> connections;
	private SimulationResults simulationResults;
	private int maxNumberConnection;
	private double simulationTime;
	private List<IsModulationFormat> modulationFormats;
	private IsRSAAlgorithm isRsaAlgorithm;
	private SimulationResultsType simulationResultsType;
	private IsLinkCostFunction linkCostFunction;
	private IsOutputLinkCostFunction outputLinkCostFunction;
	private int numberOfIterations;

	private double alfa;

	private int simulationResultSetSize;

	public Simulation() {
		super();
		this.connections = new ArrayList<Connection>();
	}

	public SimulationParameters getSimulationParameters() {
		return parameters;
	}

	public void setSimulationParams(SimulationParameters parameters) {
		this.parameters = parameters;
	}

	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public IsRoutingAlgorithm getIsRoutingAlgorithm() {
		return isRoutingAlgorithm;
	}

	public void setIsRoutingAlgorithm(IsRoutingAlgorithm isRoutingAlgorithm) {
		this.isRoutingAlgorithm = isRoutingAlgorithm;
	}

	public IsSpectrumAssignmentAlgorithm getIsAssignmentAlgorithm() {
		return isAssignmentAlgorithm;
	}

	public void setIsAssignmentAlgorithm(
			IsSpectrumAssignmentAlgorithm isAssignmentAlgorithm) {
		this.isAssignmentAlgorithm = isAssignmentAlgorithm;
	}

	public double getSimulationTime() {
		return simulationTime;
	}

	public void setMaxSimulationTime(double simulationTime) {
		this.maxSimulationTime = simulationTime;
	}

	/**
	 * @return the costFunction
	 */
	public IsCostFunction getCostFunction() {
		return costFunction;
	}

	/**
	 * @param costFunction
	 *            the costFunction to set
	 */
	public void setCostFunction(IsCostFunction costFunction) {
		this.costFunction = costFunction;
	}

	/**
	 * @return the trafficGenerator
	 */
	public TrafficGenerator getTrafficGenerator() {
		return trafficGenerator;
	}

	/**
	 * @param trafficGenerator
	 *            the trafficGenerator to set
	 */
	public void setTrafficGenerator(TrafficGenerator trafficGenerator) {
		this.trafficGenerator = trafficGenerator;
	}

	public void clearElapsedConnections() {
		if (connections != null) {
			List<Connection> tempList = new ArrayList<Connection>();

			for (Connection connection : connections) {
				if (connection.getDisconnectionTime() < simulationTime) {
					if (logger.isInfoEnabled()) {
						logger.info(MessageUtils.createMessage(
								SIMULATION_REMOVE_CONNECTION_INFO, connection
										.getPhysicalElementPair().getSource()
										.getIndex(), connection
										.getPhysicalElementPair().getTarget()
										.getIndex()));
					}
					removeConnection(connection);
				} else {
					tempList.add(connection);
				}
			}

			this.connections = tempList;
		}
	}

	private void removeConnection(Connection connection) {
		Route route = connection.getRoute();
		if (route != null && route.isPathValid()) {
			topology.disconnect(route);
		}
	}

	public void clear() {
		setSimulationTime(0);
		if (simulationResults == null) {
			simulationResults = new SimulationResults();
		} else {
			simulationResults.clear();
		}
		topology.clearOccupancy();
		// Remove all connections from Topology
		if (connections != null)
			for (Connection connection : connections) {
				removeConnection(connection);
			}
		this.connections = new ArrayList<Connection>();
		if (!topology.isClean()) {
			throw new RuntimeException(
					EonSimulatorExceptions.TOPOLOGY_NOT_CLEAN_EXCEPTION);
		}
	}

	public void setSimulationTime(double simulationTime) {
		this.simulationTime = simulationTime;

	}

	public SimulationResults getSimulationResults() {
		return simulationResults;
	}

	public void setSimulationResults(SimulationResults simulationResults) {
		this.simulationResults = simulationResults;
	}

	public boolean nextSimulation() {
		boolean isMaxArrivalRateReached = parameters.isMaxArrivalRateReached();
		if (!parameters.isMaxArrivalRateReached()) {
			parameters.incrementArrivalRate();
		}
		return !isMaxArrivalRateReached && simulationTime < maxSimulationTime;
	}

	public void addConnection(Connection connection) {
		this.connections.add(connection);
	}

	public int getMaxNumberConnection() {
		return maxNumberConnection;
	}

	public void setMaxNumberConnection(int maxNumberConnection) {
		this.maxNumberConnection = maxNumberConnection;
	}

	public double getErlangTraffic() {
		return getSimulationParameters().getConnectionCurrentArrivalRate()
				/ getSimulationParameters().getConnectionDeathRate();
	}

	public List<IsModulationFormat> getModulationFormats() {
		return modulationFormats;
	}

	public void setModulationFormats(List<IsModulationFormat> modulationFormats) {
		this.modulationFormats = modulationFormats;
	}

	public IsRSAAlgorithm getRSAAlgorithm() {
		return isRsaAlgorithm;
	}

	public void setRSAAlgorithm(IsRSAAlgorithm rsaAlgorithm) {
		this.isRsaAlgorithm = rsaAlgorithm;
	}

	public SimulationResultsType getSimulationResultsType() {
		return simulationResultsType;
	}

	public void setSimulationResultsType(
			SimulationResultsType simulationResultsType) {
		this.simulationResultsType = simulationResultsType;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;

	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public double getAlfa() {
		return this.alfa;
	}

	public void setAlfa(double alfa) {
		this.alfa = alfa;
	}

	public void clearArrivalRate() {
		parameters.clearArrivalRate();
	}

	public void setResultSetSize(int simulationResultSetSize) {
		this.simulationResultSetSize = simulationResultSetSize;

	}

	public int getSimulationResultSetSize() {
		return simulationResultSetSize;
	}

	public IsLinkCostFunction getLinkCostFunction() {
		return linkCostFunction;
	}

	public void setLinkCostFunction(IsLinkCostFunction linkCostFunction) {
		this.linkCostFunction = linkCostFunction;
	}

	public IsOutputLinkCostFunction getOutputLinkCostFunction() {
		return outputLinkCostFunction;
	}

	public void setOutputLinkCostFunction(
			IsOutputLinkCostFunction outputLinkCostFunction) {
		this.outputLinkCostFunction = outputLinkCostFunction;
	}

}
