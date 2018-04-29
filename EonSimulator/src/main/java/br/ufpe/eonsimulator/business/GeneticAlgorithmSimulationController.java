package br.ufpe.eonsimulator.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Individual;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.rsa.RSAWrapper;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulatorkernel.domain.PhysicalElementPair;

/**
 * A simple simulation approach based on creating connection requests.
 */
public class GeneticAlgorithmSimulationController extends AbstractSimulationController
		implements IsSimulationController {

	private static Logger logger = Logger.getLogger(GeneticAlgorithmSimulationController.class);
	private Map<PhysicalElementPair, List<Route>> routesMap;
	private static final int NUMBER_OF_INDIVIDUALS = 50;
	private static final double CROSSING_CONSTANT = 0.5; // a metade do
															// individuo vai ser
															// cruzada
	private static final double ROULETTE_RATE = 0.6; // 60% dos melhores
														// individuos vão ser
														// escolhidos, 40%
														// aleatorios
	private static final long NUMBER_OF_REQUESTS = 100000;

	private static final int NUMBER_OF_ITERATIONS = 10;

	// TODO: Colocar essas variaveis no simulation.properties
	private static final double MIN_ARRIVAL = 80;
	private static final double MAX_ARRIVAL = 120;
	private static final List<Integer> SIMULATION_K_LIST = new ArrayList<Integer>();
	private static final long SIMULATION_NUMBER_OF_REQUESTS = 100000;

	private List<Individual> individuals;

	@Override
	public void run(Simulation simulation) {
		// 1 - Rodar a topologia com YEN
		// 2 - Pegar suas rotas para todos os pares origem-destino, colocar num
		// map de conexao no origem-destino e conjunto de rotas (k10)
		createPhysicalPairRouteMap(simulation);

		// 3 - Criar parametro no simulation.properties de kGenetic que varia
		// entre 1, 3, 5 e 7
		// 4 - Gerar 50 individuos com sorteando as rotas
		createIndividuals();

		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			crossIndividuals();

			fitnessIndividuals(simulation);

			roulette();

			mutation();

			fitnessIndividuals(simulation);

			roulette();

			printTheBest(i);
		}

		simulation.getSimulationParameters().setConnectionMaxArrival(MAX_ARRIVAL);
		simulation.getSimulationParameters().setConnectionMinArrival(MIN_ARRIVAL);
		simulation.clearArrivalRate();
		SIMULATION_K_LIST.clear();
		SIMULATION_K_LIST.add(1);
		SIMULATION_K_LIST.add(3);
		SIMULATION_K_LIST.add(5);
		SIMULATION_K_LIST.add(7);
		for (Integer integer : SIMULATION_K_LIST) {
			simulation.clearArrivalRate();
			System.out.println("Simulação com " + integer + " rota(s) alternativa(s):");
			do {
				fitnessIndividual(simulation, individuals.get(0), integer, SIMULATION_NUMBER_OF_REQUESTS);
				System.out.println(ConvertUtils.convertToLocaleString(individuals.get(0).getBlockingProbability()));
			} while (simulation.nextSimulation());
		}
	}

	private void printTheBest(int i) {
		System.out.println("Melhor da iteração (" + i + "): "
				+ ConvertUtils.convertToLocaleString(individuals.get(0).getBlockingProbability()));

	}

	private void mutation() {
		Random random = new Random();
		for (int i = 0; i < NUMBER_OF_INDIVIDUALS; i++) {
			int individual_a_index = random.nextInt(NUMBER_OF_INDIVIDUALS);
			int individual_b_index = random.nextInt(NUMBER_OF_INDIVIDUALS);
			Individual a = individuals.get(individual_a_index);
			Individual b = individuals.get(individual_b_index);
			Individual c = crossIndividuals(a, b, Math.random());
			individuals.add(c);
		}

	}

	private Individual crossIndividuals(Individual a, Individual b, double crossRate) {
		if (crossRate > CROSSING_CONSTANT) {
			crossRate = CROSSING_CONSTANT;
		}
		Individual newIndividual = new Individual();
		int sizeA = a.getPhysicalElementPairRoutesMap().size();
		int sizeB = b.getPhysicalElementPairRoutesMap().size();
		if (sizeA != sizeB) {
			throw new RuntimeException();
		} else if (sizeA * crossRate % 2 != 0) {
			sizeA = (int) (sizeA * crossRate + 1);
		} else if (sizeA * crossRate % 2 == 0) {
			sizeA = (int) (sizeA * crossRate);
		}
		Map<PhysicalElementPair, List<Route>> source = new HashMap<PhysicalElementPair, List<Route>>();
		Map<PhysicalElementPair, List<Route>> a1 = a.getMapAt(0, sizeA);
		Map<PhysicalElementPair, List<Route>> b1 = b.getMapAt(sizeA, sizeB);
		source.putAll(a1);
		source.putAll(b1);
		newIndividual.setPhysicalElementPairRoutesMap(source);
		return newIndividual;
	}

	private void roulette() {
		Collections.sort(individuals);
		int numberOfBestsIndividuals = (int) (ROULETTE_RATE * NUMBER_OF_INDIVIDUALS * 2);
		List<Individual> bests = new ArrayList<Individual>();
		List<Individual> rest = new ArrayList<Individual>();
		for (int i = 0; i < numberOfBestsIndividuals; i++) {
			bests.add(individuals.get(i));
		}
		for (int i = numberOfBestsIndividuals; i < NUMBER_OF_INDIVIDUALS * 2; i++) {
			rest.add(individuals.get(i));
		}
		List<Individual> randomList = new ArrayList<Individual>();
		Random random = new Random();
		int numberOfRest = NUMBER_OF_INDIVIDUALS * 2 - numberOfBestsIndividuals;
		for (int i = 0; i < numberOfRest; i++) {
			int individual_a_index = random.nextInt(numberOfRest);
			randomList.add(rest.get(individual_a_index));
		}
		individuals.clear();
		individuals.addAll(bests);
		individuals.addAll(randomList);
	}

	private void fitnessIndividuals(Simulation simulation) {
		for (Individual individual : individuals) {
			simulation.clearArrivalRate();
			fitnessIndividual(simulation, individual, simulation.getSimulationParameters().getGeneticAlgorithmK(),
					NUMBER_OF_REQUESTS);
		}
	}

	private void fitnessIndividual(Simulation simulation, Individual individual, int k, long numberOfRequests) {
		simulation.clear();
		simulation.getSimulationResults().setNumberOfRequests(numberOfRequests);
		for (int numberConnectionIndex = 0; numberConnectionIndex < numberOfRequests; numberConnectionIndex++) {
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
			List<Route> routesFromSimulation = simulation.getIsRoutingAlgorithm().createRoutes(connection,
					simulation.getTopology(), simulation.getCostFunction());
			List<Route> routesFromIndividual = getRoutesFromIndividual(connection, individual);
			List<Route> routesForSimulation = setRoutesForSimulation(routesFromSimulation, routesFromIndividual, k);
			// If routing returned at least one route solution:
			if (routesForSimulation != null && !routesForSimulation.isEmpty()) {
				RSAWrapper simulationRouteWrapper = simulation.getRSAAlgorithm().getRSAWrapper(routesForSimulation,
						simulation, connection);

				if (simulationRouteWrapper != null && simulationRouteWrapper.isValid()) {
					connection.setRoute(simulationRouteWrapper.getRoute());
					simulation.getTopology().connect(simulationRouteWrapper.getRoute());
					simulation.addConnection(connection);
				} else {
					if (simulationRouteWrapper == null) {
						simulation.getSimulationResults().incrementNumberOfNetworkBlockedRequests();
					} else if (!simulationRouteWrapper.isOSNRValid()) {
						simulation.getSimulationResults().incrementNumberOfPhysicalBlocking();
					}
				}
			} else {
				simulation.getSimulationResults().incrementNumberOfBlockedRequests();
			}
			simulation
					.setSimulationTime(simulation.getTrafficGenerator().getArrivalTimeGen().getArrivalTime(simulation));
		}
		individual.setBlockingProbability(simulation.getSimulationResults().getBlockingProbability());
	}

	private List<Route> setRoutesForSimulation(List<Route> routesFromSimulation, List<Route> routesFromIndividual,
			int k) {
		List<Route> routesForSimulation = new ArrayList<Route>();
		for (int i = 0; i < k; i++) {
			Route route = routesFromIndividual.get(i);
			String index = null;
			if (route.getPath() != null) {
				index = route.getPath().getIndex();
			}
			if (index != null) {
				for (Route routeFromSimulation : routesFromSimulation) {
					if (routeFromSimulation.getPath() != null
							&& index.equals(routeFromSimulation.getPath().getIndex())) {
						routesForSimulation.add(routeFromSimulation);
						break;
					}
				}
			}
		}
		return routesForSimulation;
	}

	private List<Route> getRoutesFromIndividual(Connection connection, Individual individual) {
		return individual.getPhysicalElementPairRoutesMap().get(connection.getPhysicalElementPair());
	}

	private void crossIndividuals() {
		Random random = new Random();
		for (int i = 0; i < NUMBER_OF_INDIVIDUALS; i++) {
			int individual_a_index = random.nextInt(NUMBER_OF_INDIVIDUALS);
			int individual_b_index = random.nextInt(NUMBER_OF_INDIVIDUALS);
			Individual a = individuals.get(individual_a_index);
			Individual b = individuals.get(individual_b_index);
			Individual c = crossIndividuals(a, b);
			individuals.add(c);
		}
	}

	private Individual crossIndividuals(Individual a, Individual b) {
		return crossIndividuals(a, b, Double.MAX_VALUE);
	}

	private void createIndividuals() {
		individuals = new ArrayList<Individual>();
		for (int i = 0; i < NUMBER_OF_INDIVIDUALS; i++) {
			Individual individual = new Individual();
			for (Entry<PhysicalElementPair, List<Route>> entry : routesMap.entrySet()) {
				List<Route> routes = new ArrayList<Route>();
				routes.addAll(entry.getValue());
				Collections.shuffle(routes);
				individual.getPhysicalElementPairRoutesMap().put(entry.getKey(), routes);
			}
			individuals.add(individual);
		}
	}

	private void createPhysicalPairRouteMap(Simulation simulation) {
		routesMap = new HashMap<PhysicalElementPair, List<Route>>();
		clearSimulation(simulation, logger);
		for (int numberConnectionIndex = 0; numberConnectionIndex < simulation
				.getMaxNumberConnection(); numberConnectionIndex++) {
			Connection connection = simulation.getTrafficGenerator().createConnection(simulation);
			List<Route> routes = simulation.getIsRoutingAlgorithm().createRoutes(connection, simulation.getTopology(),
					simulation.getCostFunction());
			routesMap.put(connection.getPhysicalElementPair(), routes);
		}
	}

}
