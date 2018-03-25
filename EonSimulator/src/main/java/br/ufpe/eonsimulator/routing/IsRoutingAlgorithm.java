package br.ufpe.eonsimulator.routing;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.costFunctions.IsCostFunction;
import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Path;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Topology;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulatorkernel.domain.PhysicalElementPair;

public abstract class IsRoutingAlgorithm {

	private static Logger logger = Logger.getLogger(IsRoutingAlgorithm.class);
	private static final String ROUTE_INFO = "route.info";
	private static final String ROUTE_INFO_INDEXS = "route.info.indexs";

	public List<Route> createRoutes(Connection connection, Topology topology, IsCostFunction costFunction) {
		PhysicalElementPair physicalElementPair = connection.getPhysicalElementPair();
		List<Route> routes = null;
		if (topology.hasPath(physicalElementPair)) {
			List<Path> paths = topology.getPaths(physicalElementPair);
			routes = toRoutes(paths);
		} else {
			routes = getRoutes(connection, topology, costFunction);
			toPaths(routes, topology, physicalElementPair);
		}
		doLog(routes);
		return routes;
	}

	private void toPaths(List<Route> routes, Topology topology, PhysicalElementPair physicalElementPair) {
		for (Route route : routes) {
			topology.addPath(physicalElementPair, route);
		}

	}

	private List<Route> toRoutes(List<Path> paths) {
		List<Route> routes = new ArrayList<Route>();
		for (Path path : paths) {
			Route route = new Route();
			route.setPath(path);
			routes.add(route);
		}
		return routes;
	}

	private void doLog(List<Route> routes) {
		if (logger.isInfoEnabled() && routes != null) {
			logger.info(MessageUtils.createMessage(ROUTE_INFO));
			for (Route route : routes) {
				logger.info(MessageUtils.createMessage(ROUTE_INFO_INDEXS, route.getSeparatedElementsIndex()));
			}
		}
	}

	protected abstract List<Route> getRoutes(Connection connection, Topology topology, IsCostFunction costFunction);
}
