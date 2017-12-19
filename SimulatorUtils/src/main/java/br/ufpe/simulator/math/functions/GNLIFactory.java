package br.ufpe.simulator.math.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import br.ufpe.simulator.math.integral.DefaultBivariateIntegrator;
import br.ufpe.simulator.utils.CollectionsUtils;
import br.ufpe.simulator.utils.ConvertUtils;

public class GNLIFactory {

	public static class TSChannels {

		private List<Double> tsList;
		private int nChannels;

		public List<Double> getTsList() {
			return tsList;
		}

		public int getnChannels() {
			return nChannels;
		}

		public void normalize() {
			if (tsList != null) {
				List<Double> tsListTemp = new ArrayList<Double>();
				boolean isForAdd = false;

				// Left to Right
				for (Double ts : tsList) {
					if (!isForAdd && ts == 0) {
						nChannels--;
					} else {
						isForAdd = true;
						tsListTemp.add(ts);
					}
				}
				isForAdd = false;
				tsList.clear();
				// Right to Left
				for (int i = tsListTemp.size() - 1; i >= 0; i--) {
					Double ts = tsListTemp.get(i);
					if (!isForAdd && ts == 0) {
						nChannels--;
					} else {
						isForAdd = true;
						tsList.add(0, ts);
					}
				}

			}
		}
	}

	public interface GNLIPhysicalProperties {

		double getAlfa();

		double getLs();
	}

	public interface GNLILinkProperties {

		List<Integer> getOccupancyList();

	}

	private static final String GNLI_PROPERTY_BETA2 = "simulation.gnli.beta2";

	private static final String GNLI_PROPERTY_BETA3 = "simulation.gnli.beta3";

	private static final String GNLI_PROPERTY_GAMA = "simulation.gnli.gama";

	private static final String GNLI_PROPERTY_CHANNELSSPACING = "simulation.gnli.channelsSpacing";

	private static final String GNLI_PROPERTY_ROLLOFF = "simulation.gnli.rollOff";

	private static final String GNLI_PROPERTY_NS = "simulation.gnli.ns";

	private static final String GNLI_PROPERTY_SPECTRUMWIDTH = "simulation.gnli.spectrumWidth";

	private static final String GNLI_PROPERTY_SLOTWIDTH = "simulation.gnli.slotWidth";

	private static final String GNLI_PROPERTY_NUMBEROFPOINTS = "simulation.gnli.nPoints";

	private static final String GNLI_PROPERTY_RELATIVE_ACCURACY = "simulation.gnli.relativeAccuracy";

	private static final String GNLI_PROPERTY_ABSOLUTE_ACCURACY = "simulation.gnli.absoluteAccuracy";

	private static double BETA2;
	private static double BETA3;
	private static double GAMA;
	private static double CHANNELSSPACING;
	private static double ROLLOFF;
	private static double NS;
	private static double SPECTRUMWIDTH;
	private static double SLOTWIDTH;
	private static int NUMBEROFPOINTS;
	private static double ABSOLUTE_ACCURACY;
	private static double RELATIVE_ACCURACY;

	public static void setGnliProperties(Properties properties) {
		setBETA2(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_BETA2)));
		setBETA3(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_BETA3)));
		setGAMA(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_GAMA)));
		setCHANNELSSPACING(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_CHANNELSSPACING)));
		setROLLOFF(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_ROLLOFF)));
		setNS(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_NS)));
		setSPECTRUMWIDTH(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_SPECTRUMWIDTH)));
		setSLOTWIDTH(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_SLOTWIDTH)));
		setNUMBEROFPOINTS(ConvertUtils.convertToInteger(properties
				.getProperty(GNLI_PROPERTY_NUMBEROFPOINTS)));
		setRELATIVEACCURACY(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_RELATIVE_ACCURACY)));
		setABSOLUTEACCURACY(ConvertUtils.convertToDouble(properties
				.getProperty(GNLI_PROPERTY_ABSOLUTE_ACCURACY)));
	}

	private static void setABSOLUTEACCURACY(double absoluteAccuracy) {
		ABSOLUTE_ACCURACY = absoluteAccuracy;

	}

	private static void setRELATIVEACCURACY(double relativeAccuracy) {
		RELATIVE_ACCURACY = relativeAccuracy;
	}

	private static void setNUMBEROFPOINTS(int numberOfPoints) {
		NUMBEROFPOINTS = numberOfPoints;

	}

	public static GNLI createGnli(GNLILinkProperties linkProperties,
			GNLIPhysicalProperties physicalProperties, List<Double> powerList) {
		GNLI gnli = new GNLI();
		gnli.setAlfa(physicalProperties.getAlfa());
		gnli.setBeta2(BETA2);
		gnli.setBeta3(BETA3);
		gnli.setChannelsSpacing(CHANNELSSPACING);
		gnli.setGama(GAMA);
		gnli.setLs(physicalProperties.getLs());
		gnli.setNs(NS);
		gnli.setRollOff(ROLLOFF);
		gnli.setSpectrumWidth(SPECTRUMWIDTH);
		gnli.setNumberOfPoints(NUMBEROFPOINTS);
		TSChannels channels = createTsChannels(
				linkProperties.getOccupancyList(), SLOTWIDTH);
		gnli.setTsList(channels.tsList);
		gnli.setNumberOfChannels(channels.nChannels);
		if (!CollectionsUtils.isNullOrEmpty(powerList)) {
			gnli.setPower(CollectionsUtils.getFirst(powerList));
		}
		DefaultBivariateIntegrator defaultBivariateIntegrator = new DefaultBivariateIntegrator(
				RELATIVE_ACCURACY, ABSOLUTE_ACCURACY);
		gnli.setBivariateIntegrator(defaultBivariateIntegrator);
		return gnli;
	}

	public static double calculateGnli(GNLI gnli) {
		double value = 0;
		int nPoints = gnli.getNumberOfPoints();
		int nChannels = gnli.getNumberOfChannels();
		double cSpacing = gnli.getChannelsSpacing();
		List<Double> tsList = gnli.getTsList();
		for (int i = 0; i < tsList.size(); i++) {
			double ts = tsList.get(i);
			if (ts != 0) {
				int bandUnits = ConvertUtils
						.convertToInteger(1 / (ts * SLOTWIDTH));
				double initialFrequencySlot = (i - Math.floor(nChannels / 2))
						* (cSpacing) - SLOTWIDTH * bandUnits / 2;
				for (int j = 0; j < nPoints; j++) {
					double powerDensity = SLOTWIDTH
							* bandUnits
							/ (nPoints)
							* gnli.value((initialFrequencySlot + (SLOTWIDTH
									* bandUnits / (nPoints + 1)))
									* (j + 1));
					double gnliPower = powerDensity != 0 ? Math.pow(
							gnli.getPower() / powerDensity, 3) : 0;
					value += gnliPower;
				}
			}
		}

		return value;
	}

	public static TSChannels createTsChannels(List<Integer> tsBandList,
			double slotWidth) {
		int nChannelsTotal = 0;
		List<Double> tsList = new ArrayList<Double>();
		for (int i = 0; i < tsBandList.size(); i++) {
			int tsBand = tsBandList.get(i);
			if (tsBand == 0) {
				tsList.add(0d);
				nChannelsTotal++;
			} else {
				int index = 0;
				int lastTsBand = tsBand;
				int nChannels = 0;
				while (lastTsBand == tsBand) {
					nChannels++;
					index++;
					if ((i + index) < tsBandList.size()) {
						tsBand = tsBandList.get(i + index);
					} else {
						break;
					}
				}
				i = i + index - 1;
				nChannelsTotal++;
				tsList.add(1 / (slotWidth * nChannels));
			}
		}

		TSChannels channels = new TSChannels();
		channels.tsList = tsList;
		channels.nChannels = nChannelsTotal;
		channels.normalize();
		return channels;
	}

	public static void setBETA2(double bETA2) {
		BETA2 = bETA2;
	}

	public static void setBETA3(double bETA3) {
		BETA3 = bETA3;
	}

	public static void setGAMA(double gAMA) {
		GAMA = gAMA;
	}

	public static void setCHANNELSSPACING(double cHANNELSSPACING) {
		CHANNELSSPACING = cHANNELSSPACING;
	}

	public static void setROLLOFF(double rOLLOFF) {
		ROLLOFF = rOLLOFF;
	}

	public static void setNS(double nS) {
		NS = nS;
	}

	public static void setSPECTRUMWIDTH(double sPECTRUMWIDTH) {
		SPECTRUMWIDTH = sPECTRUMWIDTH;
	}

	public static void setSLOTWIDTH(double sLOTWIDTH) {
		SLOTWIDTH = sLOTWIDTH;
	}

}