package br.ufpe.eonsimulator.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufpe.simulatorkernel.domain.PhysicalElementPair;

public class Individual implements Comparable<Individual> {

	private Map<PhysicalElementPair, List<Route>> physicalElementPairRoutesMap;
	private double blockingProbability;

	public Individual() {
		this.physicalElementPairRoutesMap = new HashMap<PhysicalElementPair, List<Route>>();
	}

	public Map<PhysicalElementPair, List<Route>> getPhysicalElementPairRoutesMap() {
		return physicalElementPairRoutesMap;
	}

	public void setPhysicalElementPairRoutesMap(Map<PhysicalElementPair, List<Route>> physicalElementPairRoutesMap) {
		this.physicalElementPairRoutesMap = physicalElementPairRoutesMap;
	}

	public Map<PhysicalElementPair, List<Route>> getMapAt(int from, int to) {
		Map<PhysicalElementPair, List<Route>> map = new HashMap<PhysicalElementPair, List<Route>>();
		int count = 0;
		for (Entry<PhysicalElementPair, List<Route>> entry : physicalElementPairRoutesMap.entrySet()) {
			if (count >= from && count < to) {
				map.put(entry.getKey(), entry.getValue());
			} else if(count > to) {
				break;
			}
			count++;
		}
		return map;
	}

	@Override
	public int compareTo(Individual o) {
		if (o == null) {
			return -1;
		} else {
			if (blockingProbability < o.blockingProbability) {
				return -1;
			} else if (blockingProbability > o.blockingProbability) {
				return 1;
			} else {
				return 0;
			}
		}
	}


	public double getBlockingProbability() {
		return blockingProbability;
	}

	public void setBlockingProbability(double blockingProbability) {
		this.blockingProbability = blockingProbability;
	}

}
