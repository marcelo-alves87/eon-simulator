package br.ufpe.eonsimulator.rsa;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.simulator.messages.MessageUtils;

public abstract class RSAWrapper implements Comparable<RSAWrapper> {

	private static Logger logger = Logger.getLogger(RSAWrapper.class);
	private static final String SIMULATION_ROUTE_INFO = "simulation.route.info";

	protected String index;
	protected boolean isPathValid;
	protected boolean isOSNRValid;
	private Route route;
	private double requiredOSNR;
	private int nSlots;

	public RSAWrapper(String index, boolean isPathValid, boolean isOSNRValid,
			Route route, double requiredOSNR, int nSlots) {
		super();
		this.index = index;
		this.isPathValid = isPathValid;
		this.isOSNRValid = isOSNRValid;
		this.route = route;
		this.requiredOSNR = requiredOSNR;
		this.nSlots = nSlots;
		doLog();
	}

	public void doLog() {
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(SIMULATION_ROUTE_INFO,
					isPathValid, isOSNRValid, route.getSeparatedElementsIndex()));
		}
	}

	@Override
	public int compareTo(RSAWrapper other) {
		return compareToOtherEquals(other);
	}

	protected abstract int compareToOtherEquals(RSAWrapper other);

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
		RSAWrapper other = (RSAWrapper) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	public boolean isValid() {
		return isOSNRValid && isPathValid;
	}

	public Route getRoute() {
		return route;
	}

	public boolean isOSNRValid() {
		return isOSNRValid;
	}

	public boolean isPathValid() {
		return isPathValid;
	}

	public double getRequiredOSNR() {
		return requiredOSNR;
	}

	public void setRequiredOSNR(double requiredOSNR) {
		this.requiredOSNR = requiredOSNR;
	}

	public int getnSlots() {
		return nSlots;
	}

	public void setnSlots(int nSlots) {
		this.nSlots = nSlots;
	}
}