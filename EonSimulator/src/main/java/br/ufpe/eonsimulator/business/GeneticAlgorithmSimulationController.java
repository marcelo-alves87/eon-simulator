package br.ufpe.eonsimulator.business;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import br.ufpe.simulator.pso.PSOConstants;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulatorkernel.domain.Link;

/**
 * A simple simulation approach based on creating connection requests.
 */
public class GeneticAlgorithmSimulationController extends AbstractSimulationController
		implements IsSimulationController {

	// Private class for Simulation Route information
	private static Logger logger = Logger.getLogger(GeneticAlgorithmSimulationController.class);
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
	private static final String FILE_INPUT_NAME = "src/main/resources/linksCosts.txt";

	@Override
	public void run(Simulation simulation) {
		Double[] linksCost = getDefaultLinksCost(simulation);
	}

	private Double[] getDefaultLinksCost(Simulation simulation) {
		int numberOfLinks = simulation.getTopology().getLinks().size();
		Double[] linksCosts = new Double[numberOfLinks];
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_INPUT_NAME));
			for (int i = 0; i < numberOfLinks; i++) {
				linksCosts[i] = ConvertUtils.convertToDouble(bufferedReader.readLine());
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return linksCosts;

	}
}
