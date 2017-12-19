package br.ufpe.simulatorkernel.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulator.utils.StringUtil;

public class Link {
	private static Logger logger = Logger.getLogger(Link.class);

	private static final String POWER_INFO = "power.info";
	private static final String POWER_OSNR_INFO = "power.osnr.info";

	private List<IsPhysicalElement> physicalElements;
	private SlotOccupancyCollection ocSpectrumCollection; // Identify the slots
	private boolean disabled;
	private String index;
	private double cost;
	private double nAllocatedSlots;

	public Link() {
		super();
		this.physicalElements = new ArrayList<IsPhysicalElement>();
		this.index = StringUtil.generateString();
	}

	public Link(int numberSlots) {
		this();
		this.ocSpectrumCollection = new SlotOccupancyCollection(numberSlots);
	}

	public IsPhysicalElement getSource() {
		return !this.physicalElements.isEmpty() ? this.physicalElements.get(0)
				: null;
	}

	public IsPhysicalElement getTarget() {
		return !this.physicalElements.isEmpty() ? this.physicalElements
				.get(this.physicalElements.size() - 1) : null;
	}

	public SlotOccupancyCollection getOcSpectrumCollection() {
		return ocSpectrumCollection;
	}

	public void setOcSpectrumCollection(
			SlotOccupancyCollection ocSpectrumCollection) {
		this.ocSpectrumCollection = ocSpectrumCollection;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void addPhysicalElement(Object object) {
		if (object instanceof SpanSet) {
			for (Span span : ((SpanSet) object).getSpans()) {
				this.physicalElements.add(span);
			}
		} else if (object instanceof IsPhysicalElement) {
			this.physicalElements.add((IsPhysicalElement) object);
		}
	}

	public boolean isDual(Link link) {
		return link != null && link.getSourceNode().equals(getTargetNode())
				&& link.getTargetNode().equals(getSourceNode());
	}

	public Power getPhysicalElementsPower(Power power) {
		for (IsPhysicalElement isPhysicalElement : physicalElements) {
			if (power == null) {
				power = new Power(isPhysicalElement);
			} else if (!(isPhysicalElement instanceof Laser)) { // Desconsidera
																// lasers
																// fora
																// do
																// primeiro
																// link
				updatePower(power, isPhysicalElement);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(MessageUtils.createMessage(POWER_INFO,
						isPhysicalElement.getIndex(), ConvertUtils
								.convertToLocaleString(MathUtils
										.convertLinearTodBm(power.getValue())),
						ConvertUtils.convertToLocaleString(MathUtils
								.convertLinearTodBm(power.getNoise())),
						ConvertUtils.convertToLocaleString(MathUtils
								.convertLinearTodB(power.getOSNR()))));
			}
		}
		return power;
	}

	private void updatePower(Power power, IsPhysicalElement isPhysicalElement) {
		List<Double> powerList = new ArrayList<Double>();
		powerList.add(power.getValue());
		power.setValue(power.getValue() * isPhysicalElement.getG());
		power.setNoise(power.getNoise()
				* isPhysicalElement.getG()
				+ (isPhysicalElement.getLinearNoise() + isPhysicalElement
						.getNli(ocSpectrumCollection, powerList)));
		if (logger.isDebugEnabled()) {
			logger.debug(MessageUtils.createMessage(POWER_OSNR_INFO,
					ConvertUtils.convertToLocaleString(MathUtils
							.convertLinearTodBm(power.getValue())),
					ConvertUtils.convertToLocaleString(MathUtils
							.convertLinearTodBm(power.getNoise())),
					ConvertUtils.convertToLocaleString(MathUtils
							.convertLinearTodB(power.getOSNR()))));
		}
	}

	public double getDistance() {
		double distance = 0;
		if (physicalElements != null) {
			for (IsPhysicalElement element : physicalElements) {
				distance += element.getDistance();
			}
		}
		return distance;
	}

	public IsPhysicalElement getSourceNode() {
		IsPhysicalElement node = null;
		for (IsPhysicalElement isPhysicalElement : physicalElements) {
			if (isPhysicalElement.isTopologyNode()) {
				node = isPhysicalElement;
				break;
			}
		}
		return node;
	}

	public IsPhysicalElement getTargetNode() {
		IsPhysicalElement node = null;
		for (IsPhysicalElement isPhysicalElement : physicalElements) {
			if (isPhysicalElement.isTopologyNode()) {
				node = isPhysicalElement;
			}
		}
		return node;
	}

	public void setOccupancyRange(List<String> occupancyRange) {
		int iniSlot = ConvertUtils.convertToInteger(occupancyRange.get(0));
		int endSlot = ConvertUtils.convertToInteger(occupancyRange.get(1));
		this.ocSpectrumCollection.setSlotsAsOccupied(iniSlot, endSlot);
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
		Link other = (Link) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	public double getCost() {
		return cost;
	}

	public void setSlotsAsOccupied(int initialSlot, int finalSlot) {
		getOcSpectrumCollection().setSlotsAsOccupied(initialSlot, finalSlot);
		updateAllocatedSlots(initialSlot, finalSlot);
	}

	public void setSlotsAsUnoccupied(int initialSlot, int finalSlot) {
		getOcSpectrumCollection().setSlotsAsUnoccupied(initialSlot, finalSlot);
	}

	public void updateAllocatedSlots(int initialSlot, int finalSlot) {
		this.nAllocatedSlots += (finalSlot - initialSlot);
	}

	public void updateAllocatedSlots(int nSlots) {
		this.nAllocatedSlots += nSlots;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getnAllocatedSlots() {
		return nAllocatedSlots;
	}

	public void clearOccupancy() {
		nAllocatedSlots = 0;

	}
}
