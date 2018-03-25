package br.ufpe.eonsimulator.domain;

import java.util.Properties;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.costFunctions.CostFunctionFactory;
import br.ufpe.eonsimulator.costFunctions.LinkCostFunctionFactory;
import br.ufpe.eonsimulator.costFunctions.output.OutputLinkCostFunctionFactory;
import br.ufpe.eonsimulator.domain.Simulation.SimulationResultsType;
import br.ufpe.eonsimulator.modulation.ModulationFormatComparatorFactory;
import br.ufpe.eonsimulator.modulation.ModulationFormatFactory;
import br.ufpe.eonsimulator.routing.RoutingAlgorithmFactory;
import br.ufpe.eonsimulator.rsa.RSAAlgorithmFactory;
import br.ufpe.eonsimulator.spectrumAssignment.SpectrumAssignmentFactory;
import br.ufpe.eonsimulator.trafficGenerators.TrafficGeneratorFactory;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulator.utils.PropertiesUtils;

public class SimulationFactory {

	private static Logger logger = Logger.getLogger(SimulationFactory.class);

	private static final String TOPOLOGY_PROPERTIES = "src/main/resources/topology.properties";
	private static final String SIMULATION_FACTORY_INFO = "simulation.factory.info";
	private static final String NUMBER_OF_REQUESTS_KEY = "simulation.numberOfRequests";
	private static final String NUMBER_OF_ITERATIONS_KEY = "simulation.numberOfIterations";
	private static final String SIMULATION_ALFA_KEY = "simulation.alfa";
	private static final String MAX_SIMULATIONTIME_KEY = "simulation.maxSimulationTime";
	private static final String CONNECTION_DEATHRATE_KEY = "simulation.parameters.deathRate";
	private static final String CONNECTION_ISCONNECTIONEXPANDIBLE_KEY = "simulation.parameters.isConnectionExpandible";
	private static final String CONNECTION_SLOTWIDTH_KEY = "simulation.parameters.slotWidth";
	private static final String CONNECTION_MINARRIVAL_KEY = "simulation.parameters.minArrivalTime";
	private static final String CONNECTION_MAXARRIVAL_KEY = "simulation.parameters.maxArrivalTime";
	private static final String GRAPH_POINTS_KEY = "simulation.parameters.graphPoints";
	private static final String SIMULATION_RESULTS_MODE = "simulation.results.mode";
	private static final String SIMULATION_RESULTS_SETSIZE = "simulation.result.setSize";

	private static final String CONNECTION_MAXBITRATE_KEY = "simulation.parameters.maxBitRate";

	private static final String CONNECTION_MINBITRATE_KEY = "simulation.parameters.minBitRate";

	public static Simulation createSimulation(Properties properties) {
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(SIMULATION_FACTORY_INFO));
		}
		Simulation simulation = new Simulation();
		simulation.setIsAssignmentAlgorithm(SpectrumAssignmentFactory
				.createSpectrumAssignmentAlgorithm(properties));
		simulation.setIsRoutingAlgorithm(RoutingAlgorithmFactory
				.createRoutingAlgorithm(properties));
		simulation.setMaxNumberConnection(ConvertUtils
				.convertToInteger(properties
						.getProperty(NUMBER_OF_REQUESTS_KEY)));
		simulation.setAlfa(ConvertUtils.convertToDouble(properties
				.getProperty(SIMULATION_ALFA_KEY)));
		simulation.setOutputLinkCostFunction(OutputLinkCostFunctionFactory
				.createIsOutputLinkCostFunction(properties));
		simulation.setNumberOfIterations(ConvertUtils
				.convertToInteger(properties
						.getProperty(NUMBER_OF_ITERATIONS_KEY)));
		simulation.setMaxSimulationTime(getMaxSimulationTime(properties));
		simulation.setCostFunction(CostFunctionFactory
				.createCostFunction(properties));
		simulation.setModulationFormats(ModulationFormatFactory
				.createModulationFormats(properties));
		simulation.setTrafficGenerator(TrafficGeneratorFactory
				.createTrafficGenerator(properties));
		simulation.setRSAAlgorithm(RSAAlgorithmFactory.createRsaAlgorithm(
				properties, ModulationFormatComparatorFactory
						.createModulationFormatComparator(properties)));
		simulation.setResultSetSize(ConvertUtils.convertToInteger(properties
				.getProperty(SIMULATION_RESULTS_SETSIZE)));
		simulation.setLinkCostFunction(LinkCostFunctionFactory
				.createLinkCostFunction(properties));
		simulation
				.setSimulationResultsType(createSimulationResultsType(properties));
		SimulationParameters parameters = new SimulationParameters();
		parameters.setConnectionDeathRate(ConvertUtils
				.convertToDouble(properties
						.getProperty(CONNECTION_DEATHRATE_KEY)));
		parameters.setConnectionExpandible(ConvertUtils
				.convertToBoolean(properties
						.getProperty(CONNECTION_ISCONNECTIONEXPANDIBLE_KEY)));
		parameters.setConnectionSlotWidth(ConvertUtils
				.convertToDouble(properties
						.getProperty(CONNECTION_SLOTWIDTH_KEY)));
		parameters.setConnectionMaxArrival(ConvertUtils
				.convertToDouble(properties
						.getProperty(CONNECTION_MAXARRIVAL_KEY)));
		parameters.setConnectionMaxBitRate(ConvertUtils
				.convertToDouble(properties
						.getProperty(CONNECTION_MAXBITRATE_KEY)));
		parameters.setConnectionMinBitRate(ConvertUtils
				.convertToDouble(properties
						.getProperty(CONNECTION_MINBITRATE_KEY)));
		parameters.setConnectionMinArrival(ConvertUtils
				.convertToDouble(properties
						.getProperty(CONNECTION_MINARRIVAL_KEY)));
		parameters.setNumberGraphPoints(ConvertUtils
				.convertToInteger(properties.getProperty(GRAPH_POINTS_KEY)));
		simulation.setSimulationParams(parameters);
		Properties topologyProperties = PropertiesUtils
				.createPropertiesFromResource(TOPOLOGY_PROPERTIES);
		simulation.setTopology(TopologyFactory
				.createTopology(topologyProperties));
		return simulation;
	}

	private static SimulationResultsType createSimulationResultsType(
			Properties properties) {
		SimulationResultsType simulationResultsType = null;
		int type = ConvertUtils.convertToInteger(properties
				.getProperty(SIMULATION_RESULTS_MODE));
		switch (type) {
		case 0:
			simulationResultsType = SimulationResultsType.BITRATE;
			break;
		case 1:
			simulationResultsType = SimulationResultsType.ERLANG;
			break;
		case 2:
			simulationResultsType = SimulationResultsType.LINKSCOST;
			break;
		case 3:
			simulationResultsType = SimulationResultsType.SIMPLEERLANG;
			break;
		case 4:
			simulationResultsType = SimulationResultsType.SIMPLEERLANG_LINKCOST;
			break;
		default:
			break;
		}
		return simulationResultsType;
	}

	private static double getMaxSimulationTime(Properties properties) {
		double maxSimulationTime = ConvertUtils.convertToDouble(properties
				.getProperty(MAX_SIMULATIONTIME_KEY));
		return maxSimulationTime > 0 ? maxSimulationTime : Double.MAX_VALUE;
	}
}
