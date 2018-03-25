package br.ufpe.eonsimulator.domain;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.simulator.utils.CollectionsUtils;
import br.ufpe.simulator.utils.StringUtil;
import br.ufpe.simulatorkernel.domain.Link;
import br.ufpe.simulatorkernel.domain.Power;
import br.ufpe.simulatorkernel.domain.SlotOccupancyCollection;

public class Path {
	private String index;
	private List<Link> links;
	private Double osnr;
	private Double distance;
	private int numberOfActiveConnections;

	public Path() {
		super();
		this.index = StringUtil.generateString();
		this.links = new ArrayList<Link>();
	}

	public int getNumberSlots() {
		int numberSlot = 0;
		if (links != null && !links.isEmpty()) {
			numberSlot = links.get(0).getOcSpectrumCollection().getNumberSlots();
		}
		return numberSlot;
	}

	public void addLink(Link link) {
		links.add(link);
	}

	public void addLinkLastPosition(Link link) {
		this.links.add(links.size(), link);

	}

	public Link getLastLink() {
		return CollectionsUtils.getLast(links);
	}

	public Link getFirstLink() {
		return CollectionsUtils.getFirst(links);
	}

	public String getSeparatedElementsIndex() {
		String string = "";
		for (Link link : links) {
			string += link.getSourceNode().getIndex() + " " + link.getTargetNode().getIndex() + " ";
		}
		return string;
	}

	public double getOSNR() {
		if (osnr == null) {
			Power power = null;
			for (Link link : links) {
				if (link != null) {
					power = link.getPhysicalElementsPower(power);
				}
			}
			osnr = power != null ? power.getOSNR() : 0;
		}
		return osnr.doubleValue();

	}

	public double getDistance() {
		if (distance == null) {
			distance = 0d;
			for (Link link : links) {
				distance += link.getDistance();
			}
		}
		return distance;
	}

	public List<Link> getLinks() {
		return links;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	public double getCost() {
		double cost = 0;
		for (Link link : links) {
			cost += link.getCost();
		}
		return cost;
	}

	public SlotOccupancyCollection getSlotOccupancyCollection() {
		SlotOccupancyCollection collection = new SlotOccupancyCollection(getNumberSlots());
		for (Link link : links) {
			collection.mergeOccupancy(link.getOcSpectrumCollection());
		}
		return collection;
	}

	public int getNumberOfActiveConnections() {
		return numberOfActiveConnections;
	}

	public void decreaseActiveConnections() {
		numberOfActiveConnections--;
		
	}

	public void incrementActiveConnections() {
		numberOfActiveConnections++;
		
	}
}
