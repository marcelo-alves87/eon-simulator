package br.ufpe.simulatorkernel.domain;

import java.util.List;

import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.math.functions.GNLIFactory.GNLIPhysicalProperties;

public class Fiber extends IsPhysicalElement implements GNLIPhysicalProperties {

	private double alfadB;
	private double ls;

	public Fiber() {
		super();
	}

	public Fiber(double alfadB, double ls) {
		this();
		this.alfadB = alfadB; // db/km
		this.ls = ls;
	}

	public double getAlfadB() {
		return alfadB;
	}

	public void setAlfadB(double alfa) {
		this.alfadB = alfa;
	}

	public double getLs() {
		return ls;
	}

	public void setLs(double ls) {
		this.ls = ls;
	}

	@Override
	public double getG() {
		return Math.exp(-MathUtils.convertToAlfaLinear(alfadB) * ls * SF);
	}

	@Override
	public double getDistance() {
		return ls;
	}

	@Override
	public double getNli(SlotOccupancyCollection slotOccupancyCollection,
			List<Double> powerList) {
		double nli = 0;
		if (IS_NON_LINEAR_NOISE_EFFECT_ACTIVED) {
			/*
			 * GNLI gnli = GNLIFactory.createGnli(slotOccupancyCollection, this,
			 * powerList); nli = GNLIFactory.calculateGnli(gnli); if
			 * (logger.isInfoEnabled()) {
			 * logger.info(MessageUtils.createMessage(FIBER_GNLI_INFO,
			 * ConvertUtils.convertToString(MathUtils
			 * .convertLinearTodBm(nli)))); }
			 */

		}
		return nli;
	}

	@Override
	public boolean isTopologyNode() {
		return false;
	}

	public Fiber clone() {
		Fiber fiber = new Fiber();
		fiber.setAlfadB(alfadB);
		fiber.setLs(ls);
		return fiber;
	}

	@Override
	public double getAlfa() {
		return MathUtils.convertdBmToLinear(alfadB);
	}

}
