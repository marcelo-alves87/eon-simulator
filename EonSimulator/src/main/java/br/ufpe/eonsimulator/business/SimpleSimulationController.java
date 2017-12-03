package br.ufpe.eonsimulator.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.LinksCostWrapper;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.domain.Simulation.SimulationResultsType;
import br.ufpe.eonsimulator.domain.SimulationResults.SimulationRequestCount;
import br.ufpe.eonsimulator.rsa.RSAWrapper;
import br.ufpe.simulator.list.FixedArrayList;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulatorkernel.domain.Link;

/**
 * A simple simulation approach based on creating connection requests.
 */
public class SimpleSimulationController extends AbstractSimulationController implements IsSimulationController {

	// Private class for Simulation Route information
	private static Logger logger = Logger.getLogger(SimpleSimulationController.class);
	private static final String SIMULATION_ROUTE_NOT_FOUND = "simulation.route.notFound";
	private static final String SIMULATION_INVALID_PATH_INFO = "simulation.invalidPath.info";
	private static final String SIMULATION_INVALID_OSNR_INFO = "simulation.invalidOSNR.info";
	private static final String SIMULATION_VALID_PATH_OSNR_INFO = "simulation.valid.path.osnr.info";
	private static final String SIMULATION_ERLANG_INFO = "simulation.erlang.info";
	private static final String SIMULATION_RESULTS_INFO = "simulation.results.info";
	private static final String SIMULATION_BITRATE_INFO = "simulation.bitRate.info";
	private static final String SIMULATION_RESULTS_BITRATE_INFO = "simulation.results.bitRate.info";
	private static final String SIMULATION_ITERATION_INFO = "simulation.iteration.info";
	private static final String SIMULATION_LINKSCOST_INFO = "simulation.linksCost.info";
	private static final String SIMULATION_SIMPLEERLANG_LINKCOST_INFO = "simulation.results.simpleErlang.LinkCost";

	@Override
	public void run(Simulation simulation) {
		int numberOfIterations = simulation.getNumberOfIterations();
		List<LinksCostWrapper> linksCostWrappers = new FixedArrayList<LinksCostWrapper>(
				simulation.getSimulationResultSetSize());
		for (int i = 0; i < numberOfIterations; i++) {
			if (!SimulationResultsType.SIMPLEERLANG.equals(simulation.getSimulationResultsType())) {
				System.out.println(MessageUtils.createMessage(SIMULATION_ITERATION_INFO, i));
			}
			simulation.clearArrivalRate();
			if (SimulationResultsType.LINKSCOST.equals(simulation.getSimulationResultsType())) {
				simulation.getTopology().updateLinksCost(i, simulation.getLinkCostFunction(), simulation.getAlfa(),
						linksCostWrappers);
			} else {
				simulation.getTopology().updateLinksCost(i, simulation.getLinkCostFunction(), simulation.getAlfa(),
						null);
			}
			do {
				clearSimulation(simulation, logger);
				for (int numberConnectionIndex = 0; numberConnectionIndex < simulation
						.getMaxNumberConnection(); numberConnectionIndex++) {
					simulation.clearElapsedConnections(); // Removes all the
															// connections with
															// elapsed time;
					// Defines the node pair, the bit rate and the death time of
					// the
					// connection
					Connection connection = simulation.getTrafficGenerator().createConnection(simulation);
					simulation.getSimulationResults().incrementNumberOfRequests();
					simulation.getSimulationResults().incrementNumberOfRequest(connection.getRequestedBitRate());
					// Calculate the routes using the routing algorithm
					List<Route> routes = simulation.getIsRoutingAlgorithm().createRoutes(connection,
							simulation.getTopology(), simulation.getCostFunction());

					// If routing returned at least one route solution:
					if (routes != null && !routes.isEmpty()) {
						RSAWrapper simulationRouteWrapper = simulation.getRSAAlgorithm().getRSAWrapper(routes,
								simulation, connection);

						if (simulationRouteWrapper != null && simulationRouteWrapper.isValid()) {
							if (logger.isInfoEnabled()) {
								logger.info(MessageUtils.createMessage(SIMULATION_VALID_PATH_OSNR_INFO));
							}
							connection.setRoute(simulationRouteWrapper.getRoute());
							simulation.getTopology().connect(simulationRouteWrapper.getRoute());
							simulation.addConnection(connection);
						} else {
							simulation.getSimulationResults()
									.incrementNumberOfBitRateBlockedRequest(connection.getRequestedBitRate());
							if (simulationRouteWrapper == null) {
								if (logger.isInfoEnabled()) {
									logger.info(MessageUtils.createMessage(SIMULATION_INVALID_PATH_INFO));
								}
								simulation.getSimulationResults().incrementNumberOfNetworkBlockedRequests();
							} else if (!simulationRouteWrapper.isOSNRValid()) {
								if (logger.isInfoEnabled()) {
									logger.info(MessageUtils.createMessage(SIMULATION_INVALID_OSNR_INFO));
								}
								simulation.getSimulationResults().incrementNumberOfPhysicalBlocking();
							}
						}
					} else {
						if (logger.isInfoEnabled()) {
							logger.info(MessageUtils.createMessage(SIMULATION_ROUTE_NOT_FOUND,
									connection.getPhysicalElementPair().getSource().getIndex(),
									connection.getPhysicalElementPair().getTarget().getIndex()));
						}
					}
					simulation.setSimulationTime(
							simulation.getTrafficGenerator().getArrivalTimeGen().getArrivalTime(simulation));
				}
				if (SimulationResultsType.ERLANG.equals(simulation.getSimulationResultsType())) {
					System.out
							.println(MessageUtils.createMessage(SIMULATION_ERLANG_INFO, simulation.getErlangTraffic()));
					System.out.println(MessageUtils.createMessage(SIMULATION_RESULTS_INFO,
							simulation.getSimulationResults().getNumberOfRequests(),
							ConvertUtils
									.convertToLocaleString(simulation.getSimulationResults().getBlockingProbability()),
							ConvertUtils.convertToLocaleString(
									simulation.getSimulationResults().getNetworkBlockingProbability()),
							ConvertUtils.convertToLocaleString(
									simulation.getSimulationResults().getPhysicalBlockingProbability()),
							ConvertUtils.convertToLocaleString(
									simulation.getSimulationResults().getValidPhysicalRoutesRate())));
				} else if (SimulationResultsType.SIMPLEERLANG.equals(simulation.getSimulationResultsType())) {
					System.out.println(ConvertUtils
							.convertToLocaleString(simulation.getSimulationResults().getBlockingProbability()));
				} else if (SimulationResultsType.SIMPLEERLANG_LINKCOST.equals(simulation.getSimulationResultsType())) {
					List<String> linksCost = new ArrayList<String>();
					for (Link link : simulation.getTopology().getLinks()) {
						linksCost.add(ConvertUtils.convertToString(link.getCost()));
					}
					linksCostWrappers.add(new LinksCostWrapper(
							simulation.getSimulationResults().getBlockingProbability(), linksCost));
				}
			} while (simulation.nextSimulation());
		}
		if (SimulationResultsType.BITRATE.equals(simulation.getSimulationResultsType())) {
			Map<Double, SimulationRequestCount> map = simulation.getSimulationResults().getBitRateBlockingProbability();
			for (Entry<Double, SimulationRequestCount> entry : map.entrySet()) {
				System.out.println(MessageUtils.createMessage(SIMULATION_BITRATE_INFO, entry.getKey()));
				System.out.println(MessageUtils.createMessage(SIMULATION_RESULTS_BITRATE_INFO,
						ConvertUtils.convertToLocaleString(entry.getValue().getNumberOfRequest()),
						ConvertUtils.convertToLocaleString(entry.getValue().getBlockingProbability())));
			}
		} else if (SimulationResultsType.LINKSCOST.equals(simulation.getSimulationResultsType())) {
			for (LinksCostWrapper linksCostWrapper : linksCostWrappers) {
				System.out.println(MessageUtils.createMessage(SIMULATION_LINKSCOST_INFO,
						ConvertUtils.convertToLocaleString(linksCostWrapper.getMaxCost())));
				for (String cost : linksCostWrapper.getLinksCosts()) {
					System.out.println(cost);
				}
			}
		} else if (SimulationResultsType.SIMPLEERLANG_LINKCOST.equals(simulation.getSimulationResultsType())) {

			for (LinksCostWrapper linksCostWrapper : linksCostWrappers) {
				System.out.println(MessageUtils.createMessage(SIMULATION_SIMPLEERLANG_LINKCOST_INFO,
						ConvertUtils.convertToLocaleString(linksCostWrapper.getMaxCost())));
				for (String cost : linksCostWrapper.getLinksCosts()) {
					System.out.println(cost);
				}
			}
		}
	}
}
