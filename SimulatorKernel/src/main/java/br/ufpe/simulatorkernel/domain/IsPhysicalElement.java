package br.ufpe.simulatorkernel.domain;

import java.util.List;

import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.utils.StringUtil;

public abstract class IsPhysicalElement {

	private static double WDM_CENTRAL_FREQUENCY;
	protected static double SF;
	protected static boolean IS_ASE_NOISE_EFFECT_ACTIVED;
	protected static boolean IS_NON_LINEAR_NOISE_EFFECT_ACTIVED;
	protected static double G_MAX;
	private static double RS;
	protected static double G_MIN;
	private String index;
	private double g;
	private double linearNoise;

	public IsPhysicalElement() {
		super();
		this.index = StringUtil.generateString();
	}

	public double getLinearNoise() {
		return linearNoise;
	}

	public double getNli(SlotOccupancyCollection slotOccupancyCollection,
			List<Double> powerList) {
		return 0;
	}

	public double getG() {
		return g;
	}

	public void setGdB(double gdB) {
		this.g = MathUtils.converdBToLinear(gdB);
	}

	public void setG(double g) {
		this.g = g;
	}

	public double getDistance() {
		return 0;
	}

	public static double getWDMCentralFrequency() {
		return WDM_CENTRAL_FREQUENCY;
	}

	public static void setWDMCentralFrequency(double value) {
		WDM_CENTRAL_FREQUENCY = value;
	}

	public abstract boolean isTopologyNode();

	public void setLinearNoise(double noise) {
		this.linearNoise = noise;
	}

	public static void setSF(double sF) {
		SF = sF;
	}

	public static void setIsASENoiseEffectActived(
			boolean isASENoiseEffectActived) {
		IS_ASE_NOISE_EFFECT_ACTIVED = isASENoiseEffectActived;
	}

	public static void setIsNonLinearNoiseEffectActive(
			boolean isNonLinearNoiseEffectActived) {
		IS_NON_LINEAR_NOISE_EFFECT_ACTIVED = isNonLinearNoiseEffectActived;

	}

	public static double getG_MAX() {
		return G_MAX;
	}

	public static void setG_MAX(double g_MAX) {
		G_MAX = g_MAX;
	}

	public static void setRS(double rs) {
		RS = rs;

	}

	public static double getRS() {
		return RS;
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
		IsPhysicalElement other = (IsPhysicalElement) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public static void setG_MIN(double g_MIN) {
		G_MIN = g_MIN;
	}

	public static double getG_MIN() {
		return G_MIN;
	}
}
