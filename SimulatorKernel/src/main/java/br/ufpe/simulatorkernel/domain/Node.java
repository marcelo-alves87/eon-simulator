package br.ufpe.simulatorkernel.domain;

public class Node extends IsPhysicalElement {
	private int degree;

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	@Override
	public boolean isTopologyNode() {
		return true;
	}

}
