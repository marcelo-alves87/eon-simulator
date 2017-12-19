package br.ufpe.simulatorkernel.domain;

public class PhysicalElementPair {

	private IsPhysicalElement source;
	private IsPhysicalElement target;

	public IsPhysicalElement getSource() {
		return source;
	}

	public void setSource(IsPhysicalElement source) {
		this.source = source;
	}

	public IsPhysicalElement getTarget() {
		return target;
	}

	public void setTarget(IsPhysicalElement target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		PhysicalElementPair other = (PhysicalElementPair) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}
