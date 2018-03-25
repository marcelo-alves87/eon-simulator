package br.ufpe.eonsimulator.business;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.rsa.RSAWrapper;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.pso.IProblemSet;
import br.ufpe.simulator.pso.Location;
import br.ufpe.simulator.pso.PSOConstants;
import br.ufpe.simulator.pso.PSOProcess;
import br.ufpe.simulator.utils.ConvertUtils;

public class PSOSimulationController extends AbstractSimulationController implements IsSimulationController {

	protected static final String FILE_INPUT_NAME = "src/main/resources/linksCosts.txt";
	// Private class for Simulation Route information
	private static Logger logger = Logger.getLogger(PSOSimulationController.class);
	private static final String SIMULATION_ROUTE_NOT_FOUND = "simulation.route.notFound";
	private static final String SIMULATION_INVALID_PATH_INFO = "simulation.invalidPath.info";
	private static final String SIMULATION_INVALID_OSNR_INFO = "simulation.invalidOSNR.info";
	private static final String SIMULATION_VALID_PATH_OSNR_INFO = "simulation.valid.path.osnr.info";

	@Override
	public void run(final Simulation simulation) {
		final int size = simulation.getTopology().getLinks().size();
		IProblemSet problemSet = new IProblemSet() {

			@Override
			public int getProblemDimension() {
				return size;
			}

			@Override
			public double evaluate(Location location) {
				simulation.clearArrivalRate();
				simulation.getTopology().updateLinksCost(location);
				simulation.clear();
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
							if (logger.isDebugEnabled()) {
								logger.debug(MessageUtils.createMessage(SIMULATION_VALID_PATH_OSNR_INFO));
							}
							connection.setRoute(simulationRouteWrapper.getRoute());
							simulation.getTopology().connect(simulationRouteWrapper.getRoute());
							simulation.addConnection(connection);
						} else {
							simulation.getSimulationResults()
									.incrementNumberOfBitRateBlockedRequest(connection.getRequestedBitRate());
							if (simulationRouteWrapper == null) {
								if (logger.isDebugEnabled()) {
									logger.debug(MessageUtils.createMessage(SIMULATION_INVALID_PATH_INFO));
								}
								simulation.getSimulationResults().incrementNumberOfNetworkBlockedRequests();
							} else if (!simulationRouteWrapper.isOSNRValid()) {
								if (logger.isDebugEnabled()) {
									logger.debug(MessageUtils.createMessage(SIMULATION_INVALID_OSNR_INFO));
								}
								simulation.getSimulationResults().incrementNumberOfPhysicalBlocking();
							}
						}
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug(MessageUtils.createMessage(SIMULATION_ROUTE_NOT_FOUND,
									connection.getPhysicalElementPair().getSource().getIndex(),
									connection.getPhysicalElementPair().getTarget().getIndex()));
						}
					}
					simulation.setSimulationTime(
							simulation.getTrafficGenerator().getArrivalTimeGen().getArrivalTime(simulation));
				}
				return simulation.getSimulationResults().getBlockingProbability();
			}

			@Override
			public List<Double[]> getLinkCosts() {
				List<Double[]> linksCosts = new ArrayList<Double[]>();

				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_INPUT_NAME));
					for (int i = 0; i < PSOConstants.SWARM_SIZE; i++) {
						Double[] linksCost = new Double[getProblemDimension()];
						for (int j = 0; j < getProblemDimension(); j++) {
							linksCost[j] = ConvertUtils.convertToDouble(bufferedReader.readLine());
						}
						linksCosts.add(linksCost);
					}
					bufferedReader.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return linksCosts;
			}
		};
		PSOProcess p = new PSOProcess();
		p.execute(problemSet);

		PrintWriter writer;
		try {
			writer = new PrintWriter("src/main/resources/psoOutput.txt", "UTF-8");
			for (String string : p.getMessages()) {
				writer.println(string);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
