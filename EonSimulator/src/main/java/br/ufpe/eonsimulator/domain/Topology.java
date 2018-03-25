package br.ufpe.eonsimulator.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.eonsimulator.costFunctions.IsLinkCostFunction;
import br.ufpe.simulator.pso.Location;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulatorkernel.domain.IsPhysicalElement;
import br.ufpe.simulatorkernel.domain.Link;
import br.ufpe.simulatorkernel.domain.PhysicalElementPair;

public class Topology {

	private class PhysicalElementIndexPair {

		private String sourceIndex;
		private String targetIndex;

		public PhysicalElementIndexPair(String sourceIndex, String targetIndex) {
			super();
			this.sourceIndex = sourceIndex;
			this.targetIndex = targetIndex;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((sourceIndex == null) ? 0 : sourceIndex.hashCode());
			result = prime * result + ((targetIndex == null) ? 0 : targetIndex.hashCode());
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
			PhysicalElementIndexPair other = (PhysicalElementIndexPair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (sourceIndex == null) {
				if (other.sourceIndex != null)
					return false;
			} else if (!sourceIndex.equals(other.sourceIndex))
				return false;
			if (targetIndex == null) {
				if (other.targetIndex != null)
					return false;
			} else if (!targetIndex.equals(other.targetIndex))
				return false;
			return true;
		}

		private Topology getOuterType() {
			return Topology.this;
		}

	}

	private List<Link> links;
	private Map<Link, Link> dualLinkMap;
	private Map<Path, Path> dualPath;
	private List<IsPhysicalElement> physicalElements;
	private Map<PhysicalElementIndexPair, Link> indexMap;
	private Map<PhysicalElementPair, ComplexPath> pathMap;

	public Topology() {
		super();
		this.dualLinkMap = new HashMap<Link, Link>();
		this.dualPath = new HashMap<Path, Path>();
		this.links = new ArrayList<Link>();
		this.physicalElements = new ArrayList<IsPhysicalElement>();
		this.indexMap = new HashMap<Topology.PhysicalElementIndexPair, Link>();
		this.pathMap = new HashMap<PhysicalElementPair, ComplexPath>();
	}

	public List<Link> getLinks() {
		return links;
	}

	public void disconnect(Route route) {
		connect(route, false);
	}

	public void connect(Route route) {
		connect(route, true);
	}

	public void updateAllocatedSlots(Route route, int nSlots) {
		for (Link link : route.getLinks()) {
			link.updateAllocatedSlots(nSlots);
		}
	}

	private void connect(Route route, boolean doConnection) {
		for (Link link : route.getLinks()) {
			Link dualLink = getDualLink(link);
			if (doConnection) {// Insert the connection
				link.setSlotsAsOccupied(route.getInitialSlot(), route.getFinalSlot());
				if (dualLink != null)
					dualLink.setSlotsAsOccupied(route.getInitialSlot(), route.getFinalSlot());
			} else {
				// Remove the connection
				link.setSlotsAsUnoccupied(route.getInitialSlot(), route.getFinalSlot());
				if (dualLink != null)
					dualLink.setSlotsAsUnoccupied(route.getInitialSlot(), route.getFinalSlot());
			}
		}
	}

	public Route getDualRoute(Route route) {
		Route dualRoute = new Route();
		dualRoute.setInitialSlot(route.getInitialSlot());
		dualRoute.setFinalSlot(route.getFinalSlot());

		Path path = route.getPath();
		Path dualPathPath = dualPath.get(path);
		if (dualPathPath == null) {
			for (Link link : route.getLinks()) {
				dualRoute.addLinkLastPosition(getDualLink(link));
			}
			dualPathPath = dualRoute.getPath();
			dualPath.put(path, dualPathPath);
		}

		dualRoute.setPath(dualPathPath);
		return dualRoute;
	}

	private Link getDualLink(Link link) {
		Link dual = dualLinkMap.get(link);
		if (dual == null) {
			for (Link temp : links) {
				if (temp.isDual(link)) {
					dual = temp;
					break;
				}
			}
			dualLinkMap.put(link, dual);
		}
		return dual;
	}

	public boolean isClean() { // Checks whether all links are unoccupied
		boolean isClean = true;
		if (links != null) {
			for (Link link : links) {
				if (!link.getOcSpectrumCollection().hasOnlyUnoccupiedSlots()) {
					isClean = false;
					break;
				}
			}
		}
		return isClean;
	}

	public void add(Link link) {
		this.links.add(link);
		addIndexLink(link);
		addPhysicalElementIfNotExists(link.getSourceNode());
		addPhysicalElementIfNotExists(link.getTargetNode());
	}

	private void addPhysicalElementIfNotExists(IsPhysicalElement physicalElement) {
		if (!this.physicalElements.contains(physicalElement)) {
			this.physicalElements.add(physicalElement);
		}
	}

	public List<IsPhysicalElement> getPhysicalNodes() {
		return this.physicalElements;
	}

	private void addIndexLink(Link link) {
		String sourceIndex = link.getSourceNode().getIndex();
		String targetIndex = link.getTargetNode().getIndex();
		PhysicalElementIndexPair elementIndexPair = new PhysicalElementIndexPair(sourceIndex, targetIndex);
		indexMap.put(elementIndexPair, link);
	}

	public Link getLink(String sourceIndex, String targetIndex) {
		PhysicalElementIndexPair elementIndexPair = new PhysicalElementIndexPair(sourceIndex, targetIndex);
		return indexMap.get(elementIndexPair);
	}

	public int getPhysicalElementIndex(IsPhysicalElement isPhysicalElement) {
		return physicalElements.indexOf(isPhysicalElement);
	}

	public IsPhysicalElement getPhysicalElement(int index) {
		return physicalElements.get(index);
	}

	public boolean hasPath(PhysicalElementPair physicalElementPair) {
		return pathMap.containsKey(physicalElementPair);
	}

	public void addPath(PhysicalElementPair elementPair, Route route) {
		if (!pathMap.containsKey(elementPair)) {
			pathMap.put(elementPair, new ComplexPath());
		}
		pathMap.get(elementPair).add(route.getPath());
	}

	public List<Path> getPaths(PhysicalElementPair physicalElementPair) {
		return pathMap.get(physicalElementPair) != null ? pathMap.get(physicalElementPair).getPaths() : null;
	}

	public void updateLinksCost(int iteration, IsLinkCostFunction linkCostFunction, double alfa,
			List<LinksCostWrapper> linksCostWrappers) {
		if (iteration != 0) {
			double occupactionFactor = linkCostFunction.getOccupation(links);
			saveLinksCost(occupactionFactor, linksCostWrappers);
			linkCostFunction.updateLinkCost(alfa, occupactionFactor, links);
		}
	}

	private void saveLinksCost(double maxAllocatedSlots, List<LinksCostWrapper> linksCostWrappers) {
		if (linksCostWrappers != null) {
			List<String> linksCost = new ArrayList<String>();
			for (Link link : links) {
				linksCost.add(ConvertUtils.convertToString(link.getCost()));
			}
			linksCostWrappers.add(new LinksCostWrapper(maxAllocatedSlots, linksCost));
		}
	}

	public void clearOccupancy() {
		for (Link link : links) {
			link.clearOccupancy();
		}

	}

	public void updateLinksCost(Location location) {
		for (int i = 0; i < links.size(); i++) {
			Link link = links.get(i);
			double cost = location.getLoc()[i];
			link.setCost(cost);
		}

	}

}
