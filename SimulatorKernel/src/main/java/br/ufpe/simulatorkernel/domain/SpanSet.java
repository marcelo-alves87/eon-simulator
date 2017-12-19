package br.ufpe.simulatorkernel.domain;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.simulator.math.MathUtils;

public class SpanSet extends IsPhysicalElement {

	private List<Span> spans;

	public SpanSet() {
		super();
		this.spans = new ArrayList<Span>();
	}

	public void setNoiseFigure(double noiseFigure) {
		for (Span span : spans) {
			span.setNoiseFigure(noiseFigure);
		}
	}

	public void createSpans(Span span, int numberOfSpans, double a, double b) {
		double totalLength = numberOfSpans * span.getDistance();
		double totalLoss = MathUtils.converdBToLinear(totalLength
				* span.getFiber().getAlfadB());
		double ls = getLsTotal(b, totalLength, numberOfSpans);
		double g = getLineAmplifiersGTotal(a, totalLoss, numberOfSpans);
		for (int i = 0; i < numberOfSpans; i++) {
			Span clone = span.clone();
			clone.getFiber().setLs(Math.pow(b, i + 1) * ls);
			clone.getLineAmplifier().setG(Math.pow(a, i + 1) * g);
			spans.add(clone);
		}

	}

	private double getLsTotal(double b, double totalLength, int numberOfSpans) {
		return (totalLength / MathUtils.soma(b, numberOfSpans));
	}

	private double getLineAmplifiersGTotal(double a, double totalLoss,
			int numberOfSpans) {
		return MathUtils.raiz(
				totalLoss / (Math.pow(a, MathUtils.soma(numberOfSpans))),
				numberOfSpans);
	}

	public void createSpans(Span span, double lsTotal, double a, double b) {
		int numberOfSpans = (int) Math.ceil(lsTotal / span.getDistance());
		for (int i = 0; i < numberOfSpans; i++) {
			spans.add(span.clone());
		}

		double totalLoss = getTotalLoss();
		double g = getLineAmplifiersGTotal(a, totalLoss, numberOfSpans);
		double ls = getLsTotal(b, lsTotal, numberOfSpans);
		for (int i = 0; i < numberOfSpans; i++) {
			Span span2 = spans.get(i);
			span2.getFiber().setLs(Math.pow(b, i + 1) * ls);
			span2.getLineAmplifier().setG(Math.pow(a, i + 1) * g);
		}
	}

	private double getTotalLoss() {
		double totalLoss = 0;
		for (Span span : spans) {
			totalLoss += span.getDistance() * span.getFiber().getAlfadB();
		}
		return MathUtils.converdBToLinear(totalLoss);
	}

	public List<Span> getSpans() {
		return spans;
	}

	public void setSpans(List<Span> spans) {
		this.spans = spans;
	}

	@Override
	public boolean isTopologyNode() {
		return false;
	}

	@Override
	public double getDistance() {
		double distance = 0;
		for (Span span : spans) {
			distance += span.getDistance();
		}
		return distance;
	}

}
