package br.ufpe.simulatorkernel.domain;

import java.util.List;

public class Span extends IsPhysicalElement {

	private Fiber fiber;
	private LineAmplifier spanLineAmplifier;

	public Span() {
		super();
		this.spanLineAmplifier = new LineAmplifier();
	}

	public Span(Fiber fiber) {
		this();
		this.setFiber(fiber);
	}

	public Fiber getFiber() {
		return fiber;
	}

	public void setFiber(Fiber fiber) {
		this.fiber = fiber;
		this.spanLineAmplifier.setG(1 / fiber.getG());
	}

	public LineAmplifier getLineAmplifier() {
		return spanLineAmplifier;
	}

	public void setSpanLineAmplifier(LineAmplifier spanLineAmplifier) {
		this.spanLineAmplifier = spanLineAmplifier;
	}

	@Override
	public double getLinearNoise() {
		return this.spanLineAmplifier.getLinearNoise();
	}

	@Override
	public double getDistance() {
		return fiber.getDistance();
	}

	@Override
	public double getNli(SlotOccupancyCollection slotOccupancyCollection,
			List<Double> powerList) {
		return fiber.getNli(slotOccupancyCollection, powerList);
	}

	@Override
	public double getG() {
		return 1;
	}

	@Override
	public boolean isTopologyNode() {
		return false;
	}

	public void setNoiseFigure(double noiseFigure) {
		this.spanLineAmplifier.setNoiseFigure(noiseFigure);
	}

	public Span clone() {
		Span clone = new Span();
		clone.setFiber(fiber.clone());
		clone.setNoiseFigure(this.spanLineAmplifier.getNoiseFigure());
		return clone;
	}
}
