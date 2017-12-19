package br.ufpe.simulator.math.functions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class GWDM implements UnivariateFunction {
	private int numberOfChannels;
	private double channelsSpacing;
	private double rollOff;
	private List<Double> tsList;

	public GWDM(int numberOfChannels, double channelsSpacing,
			List<Double> tsList, double rollOff) {
		super();
		this.numberOfChannels = numberOfChannels;
		this.channelsSpacing = channelsSpacing;
		this.rollOff = rollOff;
		this.tsList = tsList;
	}

	private double calculateOffSet(int index) {
		return index * channelsSpacing
				- ((Math.floor(numberOfChannels - 1) / 2) * channelsSpacing);
	}

	@Override
	public double value(double f) {
		List<RaisedCosine> cosines = createCosines();
		double value = 0;
		for (RaisedCosine raisedCosine : cosines) {
			value += raisedCosine.value(f);
		}
		return value;
	}

	private List<RaisedCosine> createCosines() {
		List<RaisedCosine> cosines = new ArrayList<RaisedCosine>();

		for (int i = 0; i < numberOfChannels; i++) {
			RaisedCosine cosine = new RaisedCosine(getTs(i), rollOff,
					calculateOffSet(i));
			cosines.add(cosine);
		}
		return cosines;
	}

	private double getTs(int index) {
		double ts = 0;
		if (tsList != null && !tsList.isEmpty()) {
			if (tsList.size() == 1) {
				ts = tsList.get(0);
			} else {
				ts = tsList.get(index);
			}
		}
		return ts;
	}
}
