package br.ufpe.simulatorkernel.domain;

import org.apache.log4j.Logger;

import br.ufpe.simulator.exceptions.EonSimulatorExceptions;
import br.ufpe.simulator.math.MathConstants;
import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;

public class LineAmplifier extends IsPhysicalElement {

	private static Logger logger = Logger.getLogger(LineAmplifier.class);
	private static final String LINEAMPLIFIER_ASE_INFO = "lineAmplifier.ase.info";

	private double noiseFigure;

	@Override
	public double getLinearNoise() {
		double ase = 0;
		if (IS_ASE_NOISE_EFFECT_ACTIVED) {
			double g = getG();
			ase = MathConstants.PLANCK * IsPhysicalElement.getRS()
					* IsPhysicalElement.getWDMCentralFrequency()
					* MathUtils.converdBToLinear(noiseFigure) * (g - 1);
			if (logger.isDebugEnabled()) {
				logger.debug(MessageUtils.createMessage(LINEAMPLIFIER_ASE_INFO,
						noiseFigure, IsPhysicalElement.getRS(),
						IsPhysicalElement.getWDMCentralFrequency(), MathUtils
								.convertLinearTodB(g), ConvertUtils
								.convertToLocaleString(MathUtils
										.convertLinearTodBm(ase))));
			}
		}
		return ase;
	}

	public double getNoiseFigure() {
		return noiseFigure;
	}

	public void setNoiseFigure(double noiseFigure) {
		this.noiseFigure = noiseFigure;
	}

	@Override
	public boolean isTopologyNode() {
		return false;
	}

	@Override
	public void setG(double g) {
		super.setG(g);
	}

	@Override
	public double getG() {
		double g = super.getG();
		double gdB = MathUtils.convertLinearTodB(g);
		if (MathUtils.roudAndCompare(gdB, getG_MAX()) > 0
				|| MathUtils.roudAndCompare(gdB, getG_MIN()) < 0) {
			throw new RuntimeException(
					EonSimulatorExceptions.AMP_GAIN_OUT_OF_RANGE_EXCEPTION);
		}
		return g;
	}

}
