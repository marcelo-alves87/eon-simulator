package br.ufpe.simulator.math.functions;

import java.util.List;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.log4j.Logger;

import br.ufpe.simulator.math.integral.BivariateIntegrator;
import br.ufpe.simulator.messages.MessageUtils;
import br.ufpe.simulator.utils.ConvertUtils;

public class GNLI implements UnivariateFunction {

	private static Logger logger = Logger.getLogger(GNLI.class);
	private static final String GNLI_INFO_DIV = "gnli.info.div";
	private static final String GNLI_INFO_GAMA2 = "gnli.info.gama2";
	private static final String GNLI_INFO_LEFF = "gnli.info.leff";
	protected static final String GNLI_INFO_GWDM = "gnli.info.gwdm";
	protected static final String GNLI_INFO_GWDM12 = "gnli.info.gwdm12";
	protected static final String GNLI_INFO_RO = "gnli.info.ro";
	protected static final String GNLI_INFO_XI = "gnli.info.xi";
	protected static final String GNLI_INFO_VALUE = "gnli.info.value";

	private double gama;
	private double alfa;
	private double ls;
	private Leff leff;
	private int numberOfChannels;
	private double channelsSpacing;
	private List<Double> tsList;
	private double rollOff;
	private double beta2;
	private double beta3;
	private double ns;
	private double spectrumWidth;
	private BivariateIntegrator bivariateIntegrator;
	private int numberOfPoints;
	private double power;

	public GNLI() {
		super();
		this.leff = new Leff();
	}

	@Override
	public double value(double f) {
		final double fValue = assertNotZero(f);
		final GWDM gwdm = new GWDM(numberOfChannels, channelsSpacing, tsList,
				rollOff);
		final RoVariable roVariable = new RoVariable(alfa, ls, beta2, beta3);
		final XVariable xVariable = new XVariable(ns, beta2, beta3, ls);
		double div1627 = (double) 16 / 27;
		if (logger.isDebugEnabled()) {
			logger.debug(MessageUtils.createMessage(GNLI_INFO_DIV,
					ConvertUtils.convertToLocaleString(div1627)));
		}
		double gama2 = Math.pow(gama, 2);
		if (logger.isDebugEnabled()) {
			logger.debug(MessageUtils.createMessage(GNLI_INFO_GAMA2,
					ConvertUtils.convertToLocaleString(gama2)));
		}
		double leffValue = Math.pow(leff.value(alfa, ls), 2);
		if (logger.isDebugEnabled()) {
			logger.debug(MessageUtils.createMessage(GNLI_INFO_LEFF,
					ConvertUtils.convertToLocaleString(leffValue)));
		}
		return div1627 * gama2 * leffValue
				* bivariateIntegrator.integrate(new BivariateFunction() {

					@Override
					public double value(double f1, double f2) {
						double gwdm1 = gwdm.value(f1);
						if (logger.isDebugEnabled()) {
							logger.debug(MessageUtils.createMessage(
									GNLI_INFO_GWDM,
									ConvertUtils.convertToLocaleString(f1),
									ConvertUtils.convertToLocaleString(gwdm1)));
						}
						double gwdm2 = gwdm.value(f2);
						if (logger.isDebugEnabled()) {
							logger.debug(MessageUtils.createMessage(
									GNLI_INFO_GWDM,
									ConvertUtils.convertToLocaleString(f2),
									ConvertUtils.convertToLocaleString(gwdm2)));
						}
						double gwdm12 = gwdm.value(f1 - f2 + fValue);
						if (logger.isDebugEnabled()) {
							logger.debug(MessageUtils.createMessage(
									GNLI_INFO_GWDM12,
									ConvertUtils.convertToLocaleString(f1),
									ConvertUtils.convertToLocaleString(f2),
									ConvertUtils.convertToLocaleString(fValue),
									ConvertUtils.convertToLocaleString(gwdm12)));
						}
						double ro = roVariable.value(f1, f2, fValue);
						if (logger.isDebugEnabled()) {
							logger.debug(MessageUtils.createMessage(
									GNLI_INFO_RO,
									ConvertUtils.convertToLocaleString(f1),
									ConvertUtils.convertToLocaleString(f2),
									ConvertUtils.convertToLocaleString(fValue),
									ConvertUtils.convertToLocaleString(ro)));
						}
						double xi = xVariable.value(f1, f2, fValue);
						if (logger.isDebugEnabled()) {
							logger.debug(MessageUtils.createMessage(
									GNLI_INFO_XI,
									ConvertUtils.convertToLocaleString(f1),
									ConvertUtils.convertToLocaleString(f2),
									ConvertUtils.convertToLocaleString(fValue),
									ConvertUtils.convertToLocaleString(xi)));
						}
						double value = gwdm1 * gwdm2 * gwdm12 * ro * xi;
						if (value != 0) {
							if (logger.isDebugEnabled()) {
								logger.debug(MessageUtils.createMessage(
										GNLI_INFO_VALUE,
										ConvertUtils.convertToLocaleString(f1),
										ConvertUtils.convertToLocaleString(f2),
										ConvertUtils.convertToLocaleString(fValue),
										ConvertUtils.convertToLocaleString(value)));
							}
						}
						return value;
					}
				}, getMinSpectrum(), getMaxSpectrum(), getMinSpectrum(),
						getMaxSpectrum());
	}

	private double assertNotZero(double f) {
		if (Double.compare(f, 0) == 0) {
			return Math.pow(10, -12);
		} else {
			return f;
		}

	}

	private double getMinSpectrum() {
		return -1 * this.spectrumWidth / 2;
	}

	private double getMaxSpectrum() {
		return this.spectrumWidth / 2;
	}

	public double getGama() {
		return gama;
	}

	public void setGama(double gama) {
		this.gama = gama;
	}

	public double getAlfa() {
		return alfa;
	}

	public void setAlfa(double alfa) {
		this.alfa = alfa;
	}

	public double getLs() {
		return ls;
	}

	public void setLs(double ls) {
		this.ls = ls;
	}

	public Leff getLeff() {
		return leff;
	}

	public void setLeff(Leff leff) {
		this.leff = leff;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public void setNumberOfChannels(int numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}

	public double getChannelsSpacing() {
		return channelsSpacing;
	}

	public void setChannelsSpacing(double channelsSpacing) {
		this.channelsSpacing = channelsSpacing;
	}

	public double getRollOff() {
		return rollOff;
	}

	public void setRollOff(double rollOff) {
		this.rollOff = rollOff;
	}

	public double getBeta2() {
		return beta2;
	}

	public void setBeta2(double beta2) {
		this.beta2 = beta2;
	}

	public double getBeta3() {
		return beta3;
	}

	public void setBeta3(double beta3) {
		this.beta3 = beta3;
	}

	public double getNs() {
		return ns;
	}

	public void setNs(double ns) {
		this.ns = ns;
	}

	public double getSpectrumWidth() {
		return spectrumWidth;
	}

	public void setSpectrumWidth(double spectrumWidth) {
		this.spectrumWidth = spectrumWidth;
	}

	public BivariateIntegrator getBivariateIntegrator() {
		return bivariateIntegrator;
	}

	public void setBivariateIntegrator(BivariateIntegrator bivariateIntegrator) {
		this.bivariateIntegrator = bivariateIntegrator;
	}

	public List<Double> getTsList() {
		return tsList;
	}

	public void setTsList(List<Double> tsList) {
		this.tsList = tsList;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}

	public void setPower(double power) {
		this.power = power;

	}

	public double getPower() {
		return power;
	}

}
