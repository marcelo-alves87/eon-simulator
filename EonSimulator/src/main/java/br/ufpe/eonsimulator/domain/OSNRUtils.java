package br.ufpe.eonsimulator.domain;

import org.apache.log4j.Logger;

import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;

public class OSNRUtils {

	private static final String SIMULATION_OSNR_INFO = "simulation.osnr.info";

	private static Logger logger = Logger.getLogger(OSNRUtils.class);

	public static boolean isValidOSNR(Simulation simulation,
			Connection connection, Route route, double nSymbol) {

		double connectionOSNR = connection.getRequiredOSNR();
		double lastLinkOSNR = route.getOSNR();
		double cost = route.getCost();
		Route dualRoute = simulation.getTopology().getDualRoute(route);
		double lastDualLinkOSNR = dualRoute.getOSNR();
		double dualCost = dualRoute.getCost();
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(SIMULATION_OSNR_INFO,
					ConvertUtils.convertToLocaleString(MathUtils
							.convertLinearTodB(connectionOSNR)), ConvertUtils
							.convertToLocaleString(MathUtils
									.convertLinearTodB(lastLinkOSNR)),
					ConvertUtils.convertToLocaleString(MathUtils
							.convertLinearTodB(lastDualLinkOSNR)), ConvertUtils
							.convertToLocaleString(cost), ConvertUtils
							.convertToLocaleString(dualCost)));
		}
		return lastLinkOSNR >= connectionOSNR
				&& lastDualLinkOSNR >= connectionOSNR;
	}
}
