package br.ufpe.eonsimulator.business;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.simulator.messages.MessageUtils;

public class AbstractSimulationController {

	private static final String SIMULATION_CLEARING_INFO = "simulation.clearing.info";
	private static final String SIMULATION_START_INFO = "simulation.start.info";

	protected void clearSimulation(Simulation simulation, Logger logger) {
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(SIMULATION_START_INFO));
			logger.info(MessageUtils.createMessage(SIMULATION_CLEARING_INFO));
		}
		simulation.clear();
	}
}
