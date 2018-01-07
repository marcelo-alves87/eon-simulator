package br.ufpe.eonsimulator.rsa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.modulation.IsModulationFormat;
import br.ufpe.eonsimulator.modulation.ModulationFormatBitRateWrapper;
import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulator.utils.StringUtil;

public abstract class RSAAlgorithm implements IsRSAAlgorithm {

	protected static Logger logger = Logger.getLogger(RSAAlgorithm.class);
	protected static final String SIMULATION_ROUTE_BESTROUTE_INFO = "simulation.route.bestRoute.info";
	protected static final String SIMULATION_ROUTE_TRY_NEW_MODULATIONFORMAT_INFO = "simulation.route.tryingNewModulationFormat";

	private Comparator<ModulationFormatBitRateWrapper> modulationFormatComparator;
	private int kFilter;
	private boolean qotFilter;

	public RSAAlgorithm(Comparator<ModulationFormatBitRateWrapper> modulationFormatComparator, int kFilter,
			boolean qotFilter) {
		super();
		this.modulationFormatComparator = modulationFormatComparator;
		this.kFilter = kFilter;
		this.qotFilter = qotFilter;
	}

	public RSAWrapper getRSAWrapper(List<Route> routes, Simulation simulation, Connection connection) {
		RSAWrapper routeWrapper = null;
		boolean isValidRouteWrapper = false;
		Iterator<ModulationFormatBitRateWrapper> modulationFormatIterator = createModulationFormatIterator(simulation,
				connection);
		while (!isValidRouteWrapper && modulationFormatIterator.hasNext()) {
			routeWrapper = null;
			IsModulationFormat modulationFormat = modulationFormatIterator.next().getModulationFormat();
			connection.setNumberSlotRequired(modulationFormat.createNumberOfRequiredSlots(
					simulation.getSimulationParameters().getConnectionSlotWidth(), connection.getRequestedBitRate()));

			connection
					.setRequiredOSNR(modulationFormat.createRequiredOSNR(simulation, connection.getRequestedBitRate()));

			List<RSAWrapper> routeWrappers = createRsaWrappers(routes, simulation, connection, modulationFormat, true);
			List<RSAWrapper> pathWrappers = new ArrayList<RSAWrapper>();
			for (RSAWrapper rsaWrapper : routeWrappers) {
				if (rsaWrapper.isPathValid()) {
					pathWrappers.add(rsaWrapper);
				}
			}
			simulation.getSimulationResults().incrementNumberOfPhysicalValidRoutes(pathWrappers.size());
			for (RSAWrapper rsaWrapper : pathWrappers) {
				routeWrapper = rsaWrapper;
				connection.setNumberSlotRequired(routeWrapper.getnSlots());
				connection.setRequiredOSNR(routeWrapper.getRequiredOSNR());
				if (rsaWrapper.isValid()) {
					if (logger.isInfoEnabled()) {
						logger.info(MessageUtils.createMessage(SIMULATION_ROUTE_BESTROUTE_INFO,
								routeWrapper.getRoute().getSeparatedElementsIndex(), routeWrapper.isPathValid(),
								routeWrapper.isOSNRValid(), connection.getNumberSlotRequired(),
								connection.getRequestedBitRate(), ConvertUtils.convertToLocaleString(
										MathUtils.convertLinearTodB(connection.getRequiredOSNR()))));
					}
					isValidRouteWrapper = true;
					break;
				}

			}
			if (!isValidRouteWrapper && logger.isInfoEnabled()) {
				logger.info(MessageUtils.createMessage(SIMULATION_ROUTE_TRY_NEW_MODULATIONFORMAT_INFO,
						MathUtils.convertLinearTodB(connection.getRequiredOSNR()), connection.getNumberSlotRequired()));
			}
		}
		return routeWrapper;
	}

	public List<RSAWrapper> createRsaWrappers(List<Route> routes, Simulation simulation, Connection connection,
			IsModulationFormat modulationFormat, boolean doSpectrumAssignment) {
		List<RSAWrapper> routeWrappers = new ArrayList<RSAWrapper>();
		for (Route route : routes) {

			route.getLastLink().getTarget().getIndex();

			MathUtils.convertLinearTodB(route.getOSNR());

			if (doSpectrumAssignment) {
				// Try to assign a wavelength to each path, using the WA
				simulation.getIsAssignmentAlgorithm().trySpectrumAssignment(connection, route);
			}
			RSAWrapper routeWrapper2 = createRSAWrapperAndDoLog(StringUtil.generateString(), route, simulation,
					connection, modulationFormat, connection.getNumberSlotRequired());
			if (qotFilter) {
				if (routeWrapper2.isOSNRValid) {
					routeWrappers.add(routeWrapper2);
				}
			} else {
				routeWrappers.add(routeWrapper2);
			}
		}
		Collections.sort(routeWrappers);
		if (routeWrappers.size() > kFilter) {
			routeWrappers = routeWrappers.subList(0, kFilter);
		}
		return routeWrappers;
	}

	public Iterator<ModulationFormatBitRateWrapper> createModulationFormatIterator(Simulation simulation,
			Connection connection) {
		List<ModulationFormatBitRateWrapper> isModulationFormats = createModulationFormatBitRateWrappers(
				simulation.getModulationFormats(), connection,
				simulation.getSimulationParameters().getConnectionSlotWidth());
		Collections.sort(isModulationFormats, modulationFormatComparator);
		Iterator<ModulationFormatBitRateWrapper> modulationFormatIterator = isModulationFormats.iterator();
		return modulationFormatIterator;
	}

	private List<ModulationFormatBitRateWrapper> createModulationFormatBitRateWrappers(
			List<IsModulationFormat> modulationFormats, Connection connection, double slotWidth) {
		List<ModulationFormatBitRateWrapper> formatBitRateWrappers = new ArrayList<ModulationFormatBitRateWrapper>();
		if (modulationFormats != null) {
			for (IsModulationFormat modulationFormat : modulationFormats) {
				formatBitRateWrappers.add(new ModulationFormatBitRateWrapper(modulationFormat,
						connection.getRequestedBitRate(), slotWidth));
			}
		}
		return formatBitRateWrappers;
	}

	protected abstract RSAWrapper createRSAWrapper(String index, Route route, Simulation simulation,
			Connection connection, IsModulationFormat modulationFormat, int nSlots);

	protected RSAWrapper createRSAWrapperAndDoLog(String index, Route route, Simulation simulation,
			Connection connection, IsModulationFormat modulationFormat, int nSlots) {
		if (logger.isInfoEnabled()) {
			logger.info("Estados dos slots : " + route.getSlotOccupancyCollection().printOccupancy());
		}
		return createRSAWrapper(index, route, simulation, connection, modulationFormat, nSlots);
	}

}
