package br.ufpe.simulatorkernel.domain;

import java.util.BitSet;

import br.ufpe.simulator.exceptions.EonSimulatorExceptions;

public class SlotOccupancyCollection {

	private BitSet slots2;
	private int size;

	public SlotOccupancyCollection() {
		super();
	}

	public SlotOccupancyCollection(int numberSlots) {
		this();
		this.slots2 = new BitSet(numberSlots);
		this.size = numberSlots;
	}

	public boolean getSlot(int index) {
		return slots2.get(index);
	}

	public int getNumberSlots() {
		return size;
	}

	public boolean hasOnlyUnoccupiedSlots() {
		return slots2.isEmpty();
	}

	public BitSet getSlots() {
		return slots2;
	}

	public void setSlots(BitSet slots) {
		this.slots2 = slots;
	}

	public void setSlotsAsOccupied(int slotInitial, int slotFinal) {
		setSlotsOccupancy(slotInitial, slotFinal, true);
	}

	public void setSlotsAsUnoccupied(int slotInitial, int slotFinal) {
		setSlotsOccupancy(slotInitial, slotFinal, false);
	}

	private void setSlotsOccupancy(int slotInitial, int slotFinal,
			boolean setOccupied) {
		if (!(slotFinal >= slotInitial && slotInitial >= 0)) {
			throw new RuntimeException(
					EonSimulatorExceptions.SLOT_INDEX_NOT_VALID_EXCEPTION);
		}
		for (int i = slotInitial; i <= slotFinal; i++) {
			boolean isOccupied = slots2.get(i);
			if (!(setOccupied && !isOccupied || !setOccupied && isOccupied)) {
				throw new RuntimeException(
						EonSimulatorExceptions.SLOT_OCCUPANCY_NOT_VALID_EXCEPTION);
			} else {
				slots2.set(i, setOccupied);
			}
		}
	}

	/**
	 * 
	 * @param ocSpectrumCollection
	 */
	public void mergeOccupancy(SlotOccupancyCollection ocSpectrumCollection) {
		slots2.or(ocSpectrumCollection.slots2);
	}

	public SlotOccupancyCollection clone() {
		SlotOccupancyCollection collection = new SlotOccupancyCollection();
		collection.setSlots(this.getSlots());
		return collection;
	}

	public String printOccupancy() {
		String occupancy = "";
		for (int i = 0; i < size; i++) {
			occupancy += getSlot(i) ? "1 " : "0 ";
		}
		return occupancy;
	}
	

}
