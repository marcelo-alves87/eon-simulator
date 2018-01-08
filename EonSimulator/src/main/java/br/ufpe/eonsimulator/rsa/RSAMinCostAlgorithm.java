package br.ufpe.eonsimulator.rsa;

import java.util.Comparator;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.OSNRUtils;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.modulation.IsModulationFormat;
import br.ufpe.eonsimulator.modulation.ModulationFormatBitRateWrapper;

public class RSAMinCostAlgorithm extends RSAAlgorithm {

	public RSAMinCostAlgorithm(
			Comparator<ModulationFormatBitRateWrapper> modulationFormatComparator,
			int kFilter, boolean qotFilter) {
		super(modulationFormatComparator, kFilter, qotFilter);
	}

	private class RSAMinCostWrapper extends RSAWrapper {

		public RSAMinCostWrapper(String index, boolean isPathValid,
				boolean isOSNRValid, Route route, double requiredOSNR,
				int nSlots) {
			super(index, isPathValid, isOSNRValid, route, requiredOSNR, nSlots);
		}

		@Override
		protected int compareToOtherEquals(RSAWrapper other) {
			double thisCost = getRoute().getCost();
			double otherCost = other.getRoute().getCost();
			double thisOSNR = getRoute().getOSNR();
			double otherOSNR = other.getRoute().getOSNR();
			if (thisCost < otherCost) {
				return -1;
			} else if (thisCost > otherCost) {
				return 1;
			} else if (thisOSNR > otherOSNR) {
				return -1;
			} else if (thisOSNR < otherOSNR) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	protected RSAWrapper createRSAWrapper(String index, Route route,
			Simulation simulation, Connection connection,
			IsModulationFormat modulationFormat, int nSlots) {
		return new RSAMinCostWrapper(index, route.isPathValid(),
				OSNRUtils.isValidOSNR(simulation, connection, route,
						modulationFormat.getNSymbol()), route,
				connection.getRequiredOSNR(), nSlots);
	}

}
