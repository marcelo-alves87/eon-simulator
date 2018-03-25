package br.ufpe.eonsimulator.trafficGenerators;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.trafficGenerators.arrivalTimeGenerators.IsArrivalTimeGenerator;
import br.ufpe.eonsimulator.trafficGenerators.bitRateGenerators.IsBitRateGenerator;
import br.ufpe.eonsimulator.trafficGenerators.deathTimeGenerators.IsDeathRateGenerator;
import br.ufpe.eonsimulator.trafficGenerators.nodePairGenerators.IsNodePairGenerator;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulatorkernel.domain.PhysicalElementPair;

public class TrafficGenerator {

	private static final String TRAFFICGENERATOR_INFO = "trafficGenerator.info";

	private static Logger logger = Logger.getLogger(TrafficGenerator.class);

	protected IsNodePairGenerator trafficGen;
	protected IsBitRateGenerator bitRateGen;
	protected IsArrivalTimeGenerator arrivalTimeGen;
	protected IsDeathRateGenerator deathRateGen;

	public TrafficGenerator(IsNodePairGenerator nodePairGen,
			IsBitRateGenerator bitRateGen,
			IsArrivalTimeGenerator arrivalTimeGen,
			IsDeathRateGenerator deathRateGen) {
		super();
		this.trafficGen = nodePairGen;
		this.bitRateGen = bitRateGen;
		this.arrivalTimeGen = arrivalTimeGen;
		this.deathRateGen = deathRateGen;
	}

	/**
	 * Create a connection with a node pair, a bit rate and a death time.
	 * 
	 * @param simulation
	 * @return connection The created connection
	 */

	private Connection createConnection(Simulation simulation,
			PhysicalElementPair elementPair, Double bitRate) {
		Connection connection = new Connection();

		connection.setPhysicalElementPair(elementPair);
		// Generate bit rate
		connection.setRequestedBitRate(bitRate);

		// Generate death rate
		connection.setDisconnectionTime(deathRateGen
				.createDeathRate(simulation));

		connection.setMaxSlotsExpandUnits(simulation.getSimulationParameters()
				.getMaxSlotExpandUnits());
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(TRAFFICGENERATOR_INFO,
					connection.getPhysicalElementPair().getSource().getIndex(),
					connection.getPhysicalElementPair().getTarget().getIndex()));
		}
		return connection;
	}

	public Connection createConnection(Simulation simulation,
			PhysicalElementPair elementPair) {
		return createConnection(simulation, elementPair,
				bitRateGen.createBitRate(simulation));
	}

	public Connection createConnection(Simulation simulation) {
		return createConnection(simulation,
				trafficGen.createPhysicalPair(simulation));
	}

	public Connection createConnection(Simulation simulation, Double bitRate) {
		return createConnection(simulation,
				trafficGen.createPhysicalPair(simulation), bitRate);
	}

	/**
	 * @return the nodePairGen
	 */
	public IsNodePairGenerator getNodePairGen() {
		return trafficGen;
	}

	/**
	 * @param nodePairGen
	 *            the nodePairGen to set
	 */
	public void setNodePairGen(IsNodePairGenerator nodePairGen) {
		this.trafficGen = nodePairGen;
	}

	/**
	 * @return the bitRateGen
	 */
	public IsBitRateGenerator getBitRateGen() {
		return bitRateGen;
	}

	/**
	 * @param bitRateGen
	 *            the bitRateGen to set
	 */
	public void setBitRateGen(IsBitRateGenerator bitRateGen) {
		this.bitRateGen = bitRateGen;
	}

	/**
	 * @return the arrivalTimeGen
	 */
	public IsArrivalTimeGenerator getArrivalTimeGen() {
		return this.arrivalTimeGen;
	}

	/**
	 * @param arrivalTimeGen
	 */
	public void setArrivalTimeGen(IsArrivalTimeGenerator arrivalTimeGen) {
		this.arrivalTimeGen = arrivalTimeGen;
	}

	/**
	 * @return the deathRateGen
	 */
	public IsDeathRateGenerator getDeathRateGen() {
		return deathRateGen;
	}

	/**
	 * @param deathRateGen
	 *            the deathRateGen to set
	 */
	public void setDeathRateGen(IsDeathRateGenerator deathRateGen) {
		this.deathRateGen = deathRateGen;
	}
}
