package br.ufpe.simulatorkernel.domain;

import br.ufpe.simulator.math.MathUtils;

public class Laser extends IsPhysicalElement {

	@Override
	public boolean isTopologyNode() {
		return false;
	}

	public void setOSNR(double osnrDB) {
		if (osnrDB < 0) {
			osnrDB = Double.MAX_VALUE;
		}
		setLinearNoise(getG() / MathUtils.converdBToLinear(osnrDB));
	}

}
