package br.ufpe.eonsimulator.business;

import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.rsa.RSAWrapper;
import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulatorkernel.domain.IsPhysicalElement;
import br.ufpe.simulatorkernel.domain.PhysicalElementPair;

public class OSNRValidationSimulationController extends AbstractSimulationController implements IsSimulationController {

	// Private class for Simulation Route information
	private static Logger logger = Logger.getLogger(OSNRValidationSimulationController.class);

	@Override
	public void run(Simulation simulation) {
		clearSimulation(simulation, logger);
		List<IsPhysicalElement> physicalElements = simulation.getTopology().getPhysicalNodes();
		double maxDistance = 0;
		Connection connection = null;
		RSAWrapper maxRouteOrderingWrapper = null;
		if (physicalElements != null) {
			for (IsPhysicalElement isPhysicalElement : physicalElements) {
				for (IsPhysicalElement isPhysicalElement2 : physicalElements) {
					if (isPhysicalElement.isTopologyNode() && isPhysicalElement2.isTopologyNode()
							&& !isPhysicalElement.equals(isPhysicalElement2)) {
						PhysicalElementPair elementPair = new PhysicalElementPair();
						elementPair.setSource(isPhysicalElement);
						elementPair.setTarget(isPhysicalElement2);
						// Defines the node pair, the bit rate and the death
						// time of the
						// connection
						connection = simulation.getTrafficGenerator().createConnection(simulation, elementPair);
						simulation.getSimulationResults().incrementNumberOfRequests();
						// Calculate the routes using the routing algorithm
						List<Route> routes = simulation.getIsRoutingAlgorithm().createRoutes(connection,
								simulation.getTopology(), simulation.getCostFunction());

						// If routing returned at least one route solution:
						if (routes != null && !routes.isEmpty()) {
							RSAWrapper simulationRouteWrapper = simulation.getRSAAlgorithm().getRSAWrapper(routes,
									simulation, connection);

							double routeDistance = simulationRouteWrapper.getRoute().getDistance();
							if (routeDistance > maxDistance) {
								maxRouteOrderingWrapper = simulationRouteWrapper;
								maxDistance = routeDistance;
							}
							if (!simulationRouteWrapper.isOSNRValid()) {
								simulation.getSimulationResults().incrementNumberOfPhysicalBlocking();
							}
						}
					}
				}
			}

		}

		System.out.println("Physical blocking percent: " + ConvertUtils
				.convertToLocaleString(simulation.getSimulationResults().getPhysicalBlockingProbability() * 100));
		System.out.println("Number of Required Slots: " + connection.getNumberSlotRequired());
		if (maxRouteOrderingWrapper != null) {
			System.out.println("Max Route Distance: " + maxDistance);
			System.out.println(
					"Max Route OSNR: " + MathUtils.convertLinearTodB(maxRouteOrderingWrapper.getRoute().getOSNR()));
			System.out.println("OSNRreq: " + MathUtils.convertLinearTodB(maxRouteOrderingWrapper.getRequiredOSNR()));
			System.out.println("Max Route: " + maxRouteOrderingWrapper.getRoute().getSeparatedElementsIndex());
		}
	}
}
