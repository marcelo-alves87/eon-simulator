package br.ufpe.simulatorkernel.domain;

import java.util.Properties;

import br.ufpe.simulator.utils.ConvertUtils;

public class PhysicalLayerProperties {

	public static final String WDM_CENTRAL_FREQUENCY_KEY = "simulation.physicalLayer.centralFrequency";
	public static final String RS_KEY = "simulation.rs";
	public static final String SF_KEY = "simulation.physicalLayer.sf";
	public static final String ASE_NOISE_EFFECT_KEY = "simulation.aseNoiseEffect.on";
	public static final String NON_LINEAR_NOISE_EFFECT_KEY = "simulation.nonLinearNoiseEffect.on";
	public static final String G_MAX_KEY = "simulation.physicalLayer.gMax";
	public static final String G_MIN_KEY = "simulation.physicalLayer.gMin";

	public static void setProperties(Properties properties) {
		IsPhysicalElement.setWDMCentralFrequency(ConvertUtils
				.convertToDouble(properties
						.getProperty(WDM_CENTRAL_FREQUENCY_KEY)));
		IsPhysicalElement.setSF(ConvertUtils.convertToDouble(properties
				.getProperty(SF_KEY)));
		IsPhysicalElement.setRS(ConvertUtils.convertToDouble(properties
				.getProperty(RS_KEY)));
		IsPhysicalElement
				.setIsASENoiseEffectActived(ConvertUtils
						.convertToBoolean(properties
								.getProperty(ASE_NOISE_EFFECT_KEY)));
		IsPhysicalElement.setIsNonLinearNoiseEffectActive(ConvertUtils
				.convertToBoolean(properties
						.getProperty(NON_LINEAR_NOISE_EFFECT_KEY)));
		IsPhysicalElement.setG_MAX(ConvertUtils.convertToDouble(properties
				.getProperty(G_MAX_KEY)));
		IsPhysicalElement.setG_MIN(ConvertUtils.convertToDouble(properties
				.getProperty(G_MIN_KEY)));
	}
}
