package br.ufpe.eonsimulator.business;

import java.util.Properties;

import br.ufpe.simulator.utils.ConvertUtils;

public class SimulationControllerFactory {

	private static final String SIMULATORTYPE = "simulation.simulator";

	public static IsSimulationController createSimulationController(
			Properties properties) {
		IsSimulationController simulationController = null;
		int simulatorType = ConvertUtils.convertToInteger(properties
				.getProperty(SIMULATORTYPE));
		switch (simulatorType) {
		case 0:
			simulationController = new SimpleSimulationController();
			break;
		case 1:
			simulationController = new OSNRValidationSimulationController();
			break;
		case 2:
			simulationController = new EqualsNumberRoutesSimulationController();
			break;
		case 3:
			simulationController = new OfflineSimulationController();
			break;
		case 4:
			simulationController = new PSOSimulationController();
			break;
		case 5:
			simulationController = new GeneticAlgorithmSimulationController();
			break;
		default:
			break;
		}
		return simulationController;
	}
}
