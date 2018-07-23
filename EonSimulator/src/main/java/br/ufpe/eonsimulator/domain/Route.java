package br.ufpe.eonsimulator.domain;

import java.util.List;

import br.ufpe.simulatorkernel.domain.Link;
import br.ufpe.simulatorkernel.domain.SlotOccupancyCollection;

public class Route {
	private Path path;
	private int initialSlot;
	private int finalSlot;
	private Double osnr;

	public Route() {
		clear();
	}

	public int getNumberSlots() {
		return getPath().getNumberSlots();
	}

	public boolean isPathValid() {
		return initialSlot > -1 && finalSlot > -1 ? true : false;
	}

	public int getInitialSlot() {
		return initialSlot;
	}

	public void setInitialSlot(int initialSlot) {
		this.initialSlot = initialSlot;
	}

	public int getFinalSlot() {
		return finalSlot;
	}

	public void setFinalSlot(int finalSlot) {
		this.finalSlot = finalSlot;
	}

	public void addLink(Link link) {
		getPath().addLink(link);
	}

	public void addLinkLastPosition(Link link) {
		getPath().addLinkLastPosition(link);
	}

	public Link getLastLink() {
		return getPath().getLastLink();
	}

	public Link getFirstLink() {
		return getPath().getFirstLink();
	}

	public String getSeparatedElementsIndex() {
		return getPath().getSeparatedElementsIndex();
	}

	public double getOSNR() {
		return this.osnr != null ? this.osnr : getPath().getOSNR();
	}

	public double getDistance() {
		return getPath().getDistance();
	}

	public List<Link> getLinks() {
		return getPath().getLinks();
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Path getPath() {
		if (path == null) {
			path = new Path();
		}
		return path;
	}

	public void setOsnr(Double osnr) {
		this.osnr = osnr;
	}

	public int getNHops() {
		return path.getLinks().size();
	}

	public double getCost() {
		return path.getCost();
	}

	public SlotOccupancyCollection getSlotOccupancyCollection() {
		return path.getSlotOccupancyCollection();
	}

	public void clear() {
		this.initialSlot = -1;
		this.finalSlot = -1;
	}
	
	

}
