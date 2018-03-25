package br.ufpe.eonsimulator.modulation;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.messages.MessageUtils;

public abstract class QAMModulationFormat implements IsModulationFormat {

	private static Logger logger = Logger.getLogger(QAMModulationFormat.class);
	private static final String QAMMODULATION_OSNR_KEY = "modulation.requiredOSNR";
	private static final String QAMMODULATION_NSLOTS_KEY = "modulation.requiredNSlots";

	@Override
	public double createRequiredOSNR(Simulation simulation, double bitRate) {
		double slotWidth = simulation.getSimulationParameters()
				.getConnectionSlotWidth();
		double osnr = bitRate * MathUtils.converdBToLinear(getRequiredSNRdB())
				/ (2 * slotWidth);
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(QAMMODULATION_OSNR_KEY,
					bitRate, MathUtils.convertLinearTodB(osnr)));
		}
		return osnr;
	}

	protected abstract int getM();

	@Override
	public int createNumberOfRequiredSlots(double slotWidth, double bitRate) {
		int nSlots = (int) Math.ceil((bitRate / getNSymbol()) / slotWidth);
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(QAMMODULATION_NSLOTS_KEY,
					nSlots));
		}
		return nSlots;
	}

	@Override
	public double getNSymbol() {
		return MathUtils.log2(getM());
	}

}