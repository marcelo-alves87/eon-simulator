package org.ufpe.simulationtopologyfactory.business;

import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulator.utils.PropertiesUtils;
import br.ufpe.simulator.utils.StringUtil;

public class DefaulSimulationTopologyFactory implements
		IsSimulationTopologyFactory {

	private static final String TOPOLOGY_FACTORY_PROPERTIES = "src/main/resources/topologyFactory.properties";
	private static final String PROPERTY_NUMBER_OF_NODES = "numberOfNodes";
	private static final String PROPERTY_LASER_GAIN = "laserGain";
	private static final String PROPERTY_OSNR_IN = "osnrIn";
	private static final String PROPERTY_NUMBER_OF_LINKS = "numberoOfLinks";
	private static final String PROPERTY_NODE_LOSS = "nodeLoss";
	private static final String TYPE_SUFFIX = ".type";
	private static final String TOPOLOGY_ELEMENT_PREFIX = "simulation.topology.element";
	private static final String GAIN_SUFFIX = ".gain";
	private static final String OSNR_SUFFIX = ".osnr";
	private static final String G_SUFFIX = ".g";
	private static final String INDEX_SUFFIX = ".index";
	private static final String OUTPUT_FILE_NAME = "outPutFileName";
	private static final String TOPOLOGY_NAME = "topologyName";
	private static final String FILE_PATH = "src/main/resources/";
	private static final String NUMBER_OF_ELEMENTS = "simulation.topology.numberOfElements";
	private static final String NUMBER_OF_LINKS = "simulation.topology.numberOfLinks";
	private static final String LOSS_COEFFICIENT_SUFFIX = ".lossCoefficient";
	private static final String PROPERTY_LOSS_COEFFICIENT = "lossCoeficient";
	private static final String TOTAL_LENGTH_SUFFIX = ".totalLength";
	private static final String LENGTH_SUFFIX = ".length";
	private static final String PROPERTY_SECTION_SPAN_LENGTH = "sectionSpanWidth";
	private static final String A_SUFFIX = ".a";
	private static final String PROPERTY_A = "coefficienta";
	private static final String B_SUFFIX = ".b";
	private static final String PROPERTY_B = "coefficientb";
	private static final String NOISE_FIGURE_SUFFIX = ".noiseFigure";
	private static final String PROPERTY_NOISE_FIGURE = "noiseFigure";
	private static final String L_PREFFIX = "l";
	private static final String TOPOLOGY_LINK_PREFIX = "simulation.topology.link";
	private static final String NUMBER_OF_SLOTS_SUFFIX = ".numberOfSlots";
	private static final Object PROPERTY_NUMBER_OF_SLOTS = "numberOfSlots";
	private static final String COST_SUFFIX = ".cost";
	private static final Object PROPERTY_COST = "linkCost";
	private static final String ELEMENTS_SUFFIX = ".elements";
	private static final String ELEMENT_PREFIX = "element";

	@Override
	public void create() {
		Properties properties = PropertiesUtils
				.createPropertiesFromResource(TOPOLOGY_FACTORY_PROPERTIES);
		Properties topology = new Properties();

		int numberOfNodes = ConvertUtils.convertToInteger(properties
				.getProperty(PROPERTY_NUMBER_OF_NODES));
		int numberOfLinks = ConvertUtils.convertToInteger(properties
				.getProperty(PROPERTY_NUMBER_OF_LINKS)) * 2;
		int numberOfElements = numberOfNodes + numberOfLinks + 1;

		topology.setProperty(NUMBER_OF_ELEMENTS,
				ConvertUtils.convertToString(numberOfElements));

		topology.setProperty(NUMBER_OF_LINKS,
				ConvertUtils.convertToString(numberOfLinks));

		createNodes(topology, properties, numberOfNodes);

		createLinks(topology, properties, numberOfLinks, numberOfNodes);

		createTopolyFile(properties.getProperty(OUTPUT_FILE_NAME), topology,
				properties.getProperty(TOPOLOGY_NAME));

	}

	private void createLink(int index, int i, String source, String target,
			String length, Properties topology, Properties properties) {
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index + TYPE_SUFFIX,
				ConvertUtils.convertToString(6));
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index
				+ LOSS_COEFFICIENT_SUFFIX, ConvertUtils
				.convertToString(properties.get(PROPERTY_LOSS_COEFFICIENT)));
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index + LENGTH_SUFFIX,
				ConvertUtils.convertToString(properties
						.get(PROPERTY_SECTION_SPAN_LENGTH)));
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index + A_SUFFIX,
				ConvertUtils.convertToString(properties.get(PROPERTY_A)));
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index + B_SUFFIX,
				ConvertUtils.convertToString(properties.get(PROPERTY_B)));
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index
				+ NOISE_FIGURE_SUFFIX, ConvertUtils.convertToString(properties
				.get(PROPERTY_NOISE_FIGURE)));
		topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + index
				+ TOTAL_LENGTH_SUFFIX, ConvertUtils.convertToString(length));
		topology.setProperty(TOPOLOGY_LINK_PREFIX + i + NUMBER_OF_SLOTS_SUFFIX,
				ConvertUtils.convertToString(properties
						.get(PROPERTY_NUMBER_OF_SLOTS)));
		topology.setProperty(TOPOLOGY_LINK_PREFIX + i + COST_SUFFIX,
				ConvertUtils.convertToString(properties.get(PROPERTY_COST)));
		topology.setProperty(TOPOLOGY_LINK_PREFIX + i + ELEMENTS_SUFFIX,
				ELEMENT_PREFIX + 0 + "," + ELEMENT_PREFIX + source + ","
						+ ELEMENT_PREFIX + index + "," + ELEMENT_PREFIX
						+ target);
	}

	private void createLinks(Properties topology, Properties properties,
			int numberOfLinks, int numberOfNodes) {

		int lastIndex = numberOfNodes + 1;
		int nLinks = numberOfLinks / 2;
		int index = 0;
		for (int i = 0; i < nLinks; i++) {
			index = lastIndex + i;
			List<String> params = StringUtil.split(
					properties.getProperty(L_PREFFIX + i), " ");
			createLink(index, i, params.get(0), params.get(1), params.get(2),
					topology, properties);
			createLink(index + 1, i + nLinks, params.get(1), params.get(0),
					params.get(2), topology, properties);
			lastIndex++;
		}

	}

	private void createTopolyFile(String fileName, Properties topology,
			String topologyName) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(FILE_PATH + fileName, "UTF-8");
			topology.store(writer, topologyName);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createNodes(Properties topology, Properties properties,
			int numberOfNodes) {

		double laserGain = ConvertUtils.convertToDouble(properties
				.getProperty(PROPERTY_LASER_GAIN));
		double osnrIn = ConvertUtils.convertToDouble(properties
				.getProperty(PROPERTY_OSNR_IN));
		double nodeLoss = ConvertUtils.convertToDouble(properties
				.getProperty(PROPERTY_NODE_LOSS));

		for (int i = 0; i <= numberOfNodes; i++) {
			if (i == 0) {
				topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + i + TYPE_SUFFIX,
						ConvertUtils.convertToString(5));
				topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + i + GAIN_SUFFIX,
						ConvertUtils.convertToString(laserGain));
				topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + i + OSNR_SUFFIX,
						ConvertUtils.convertToString(osnrIn));
			} else {
				topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + i + TYPE_SUFFIX,
						ConvertUtils.convertToString(0));
				topology.setProperty(TOPOLOGY_ELEMENT_PREFIX + i + G_SUFFIX,
						ConvertUtils.convertToString(nodeLoss));
				topology.setProperty(
						TOPOLOGY_ELEMENT_PREFIX + i + INDEX_SUFFIX,
						ConvertUtils.convertToString(i));
			}
		}
	}
}
