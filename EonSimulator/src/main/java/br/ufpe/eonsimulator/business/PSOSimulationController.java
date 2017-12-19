package br.ufpe.eonsimulator.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.rsa.RSAWrapper;
import br.ufpe.simulator.pso.IProblemSet;
import br.ufpe.simulator.pso.Location;
import br.ufpe.simulator.pso.PSOProcess;
import br.ufpe.simulator.pso.ProblemRange;

public class PSOSimulationController extends AbstractSimulationController
		implements IsSimulationController {

	// Private class for Simulation Route information
	private static Logger logger = Logger
			.getLogger(PSOSimulationController.class);

	@Override
	public void run(final Simulation simulation) {
		final int size = simulation.getTopology().getLinks().size();
		IProblemSet problemSet = new IProblemSet() {

			@Override
			public List<ProblemRange> getProblemRanges() {
				List<ProblemRange> problemRanges = new ArrayList<ProblemRange>();
				for (int i = 0; i < size; i++) {
					ProblemRange problemRange = new ProblemRange();
					problemRange.setHigh(1);
					problemRange.setLow(0.001);
					problemRange.setVelocityHigh(100);
					problemRange.setVelocityLow(0.1);
					problemRanges.add(problemRange);
				}
				return problemRanges;
			}

			@Override
			public int getProblemDimension() {
				return size;
			}

			@Override
			public double evaluate(Location location) {
				simulation.clearArrivalRate();
				simulation.getTopology().updateLinksCost(location);
				clearSimulation(simulation, logger);
				for (int numberConnectionIndex = 0; numberConnectionIndex < simulation
						.getMaxNumberConnection(); numberConnectionIndex++) {
					simulation.clearElapsedConnections(); // Removes all the
															// connections with
															// elapsed time;
					// Defines the node pair, the bit rate and the death time of
					// the
					// connection
					Connection connection = simulation.getTrafficGenerator()
							.createConnection(simulation);
					simulation.getSimulationResults()
							.incrementNumberOfRequests();
					simulation.getSimulationResults().incrementNumberOfRequest(
							connection.getRequestedBitRate());
					// Calculate the routes using the routing algorithm
					List<Route> routes = simulation.getIsRoutingAlgorithm()
							.createRoutes(connection, simulation.getTopology(),
									simulation.getCostFunction());

					// If routing returned at least one route solution:
					if (routes != null && !routes.isEmpty()) {
						RSAWrapper simulationRouteWrapper = simulation
								.getRSAAlgorithm().getRSAWrapper(routes,
										simulation, connection);

						if (simulationRouteWrapper != null
								&& simulationRouteWrapper.isValid()) {
							connection.setRoute(simulationRouteWrapper
									.getRoute());
							simulation.getTopology().connect(
									simulationRouteWrapper.getRoute());
							simulation.addConnection(connection);
						} else {
							simulation.getSimulationResults()
									.incrementNumberOfBitRateBlockedRequest(
											connection.getRequestedBitRate());
							if (simulationRouteWrapper == null) {
								simulation
										.getSimulationResults()
										.incrementNumberOfNetworkBlockedRequests();
							} else if (!simulationRouteWrapper.isOSNRValid()) {
								simulation.getSimulationResults()
										.incrementNumberOfPhysicalBlocking();
							}
						}
					}
					simulation.setSimulationTime(simulation
							.getTrafficGenerator().getArrivalTimeGen()
							.getArrivalTime(simulation));
				}
				return simulation.getSimulationResults()
						.getBlockingProbability();
			}
		};
		new PSOProcess().execute(problemSet);
	}
}
