package br.ufpe.simulatorkernel.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import br.ufpe.simulator.math.MathUtils;
import br.ufpe.simulator.utils.ConvertUtils;
import br.ufpe.simulator.utils.StringUtil;

public class LinksFactory {

	private static final String NUMBER_OF_ELEMENTS_KEY = "simulation.topology.numberOfElements";
	private static final String ELEMENT_PREFIX_KEY = "simulation.topology.element";
	private static final String ELEMENT_TYPE_SUFFIX_KEY = ".type";
	private static final String ELEMENT_G_SUFFIX_KEY = ".g";
	private static final String ELEMENT_MAP_KEY = "element";
	private static final String ELEMENT_SPAN_LOSSCOEFFICIENT_KEY = ".lossCoefficient";
	private static final String ELEMENT_SPAN_LENGTH_KEY = ".length";
	private static final String NUMBER_OF_LINKS_KEY = "simulation.topology.numberOfLinks";
	private static final String LINK_PREFFIX_KEY = "simulation.topology.link";
	private static final String LINK_NUMBER_OF_SLOTS_SUFFIX_KEY = ".numberOfSlots";
	private static final String LINK_COST_SUFFIX_KEY = ".cost";
	private static final String LINK_OCCUPANCY_RANGE_SUFFIX_KEY = ".occupancyRange";
	private static final String LINK_OCCUPANCY_RANGE_LENGTH_SUFFIX_KEY = ".occupancyRangeLength";
	private static final String LINK_ELEMENTS_SUFFIX_KEY = ".elements";
	private static final String LINK_ELEMENT_REGEX = ",";
	private static final String ELEMENT_LINEAMPLIFIER_NOISEFIGURE_KEY = ".noiseFigure";
	private static final String ELEMENT_LINEAMPLIFIER_G_KEY = ".g";
	private static final String ELEMENT_LASER_G_KEY = ".gain";
	private static final String ELEMENT_LASER_OSNR_KEY = ".osnr";
	private static final String ELEMENT_SPANSET_NUMBEROFSPANS_KEY = ".numberOfSpans";
	private static final String ELEMENT_SPAN_TOTAL_LENGTH_KEY = ".totalLength";
	private static final String ELEMENT_SPANSET_A_KEY = ".a";
	private static final String ELEMENT_SPANSET_B_KEY = ".b";
	private static final String ELEMENT_INDEX_SUFFIX_KEY = ".index";

	public static List<Link> createLinks(Properties properties) {
		return createLinks(properties, createElements(properties));
	}

	private static List<Link> createLinks(Properties properties,
			Map<String, Object> physicalElements) {
		List<Link> links = new ArrayList<Link>();
		int numberOfLinks = ConvertUtils.convertToInteger(properties
				.getProperty(NUMBER_OF_LINKS_KEY));
		int numberOfSlots = 0;
		double cost = 0;
		for (int index = 0; index < numberOfLinks; index++) {
			numberOfSlots = ConvertUtils.convertToInteger(properties
					.getProperty(LINK_PREFFIX_KEY + index
							+ LINK_NUMBER_OF_SLOTS_SUFFIX_KEY));
			cost = ConvertUtils.convertToDouble(properties
					.getProperty(LINK_PREFFIX_KEY + index
							+ LINK_COST_SUFFIX_KEY));
			Link link = new Link(numberOfSlots);
			link.setCost(cost);
			List<String> elements = StringUtil.split(
					properties.getProperty(LINK_PREFFIX_KEY + index
							+ LINK_ELEMENTS_SUFFIX_KEY), LINK_ELEMENT_REGEX);
			for (String elementName : elements) {
				Object object = physicalElements.get(elementName);
				link.addPhysicalElement(object);
			}

			int occupancyRangeLength = ConvertUtils.convertToInteger(properties
					.getProperty(LINK_PREFFIX_KEY + index
							+ LINK_OCCUPANCY_RANGE_LENGTH_SUFFIX_KEY));
			for (int i = 0; i < occupancyRangeLength; i++) {
				List<String> occupancyRange = StringUtil.split(
						properties.getProperty(LINK_PREFFIX_KEY + index
								+ LINK_OCCUPANCY_RANGE_SUFFIX_KEY + i),
						LINK_ELEMENT_REGEX);
				link.setOccupancyRange(occupancyRange);
			}
			links.add(link);
		}
		return links;
	}

	private static Map<String, Object> createElements(Properties properties) {
		Map<String, Object> map = new HashMap<String, Object>();
		int numberOfElements = ConvertUtils.convertToInteger(properties
				.getProperty(NUMBER_OF_ELEMENTS_KEY));
		for (int index = 0; index < numberOfElements; index++) {
			IsPhysicalElement isPhysicalElement = createElement(index,
					properties);
			if (isPhysicalElement instanceof CompensatedNode) {
				setCompensatedNodeProperties(index,
						(CompensatedNode) isPhysicalElement, properties);
				setPhysicalElementProperties(map, index,
						(IsPhysicalElement) isPhysicalElement);
			} else if (isPhysicalElement instanceof Node) {
				setNodeProperties(index, (Node) isPhysicalElement, properties);
				setPhysicalElementProperties(map, index,
						(IsPhysicalElement) isPhysicalElement);
			} else if (isPhysicalElement instanceof Span) {
				setSpanProperties(index, (Span) isPhysicalElement, properties);
				setPhysicalElementProperties(map, index,
						(IsPhysicalElement) isPhysicalElement);
			} else if (isPhysicalElement instanceof SpanSet) {
				SpanSet spanSet = (SpanSet) isPhysicalElement;
				setSpanSetProperties(index, spanSet, properties);
				map.put(ELEMENT_MAP_KEY + index, spanSet);
			} else if (isPhysicalElement instanceof Fiber) {
				setFiberProperties(index, (Fiber) isPhysicalElement, properties);
				setPhysicalElementProperties(map, index,
						(IsPhysicalElement) isPhysicalElement);
			} else if (isPhysicalElement instanceof LineAmplifier) {
				setLineAmplifierProperties(index,
						(LineAmplifier) isPhysicalElement, properties);
				setPhysicalElementProperties(map, index,
						(IsPhysicalElement) isPhysicalElement);
			} else if (isPhysicalElement instanceof Laser) {
				setLaserProperties(index, (Laser) isPhysicalElement, properties);
				setPhysicalElementProperties(map, index,
						(IsPhysicalElement) isPhysicalElement);
			}

		}
		return map;
	}

	private static void setPhysicalElementProperties(Map<String, Object> map,
			int index, IsPhysicalElement isPhysicalElement) {
		map.put(ELEMENT_MAP_KEY + index, isPhysicalElement);
	}

	private static void setSpanSetProperties(int index,
			SpanSet isPhysicalElement, Properties properties) {

		String numberOfSpansString = properties.getProperty(ELEMENT_PREFIX_KEY
				+ index + ELEMENT_SPANSET_NUMBEROFSPANS_KEY);
		if (!StringUtil.isNullOrEmpty(numberOfSpansString)) {
			isPhysicalElement.createSpans(
					new Span(new Fiber(ConvertUtils.convertToDouble(properties
							.getProperty(ELEMENT_PREFIX_KEY + index
									+ ELEMENT_SPAN_LOSSCOEFFICIENT_KEY)),
							ConvertUtils.convertToDouble(properties
									.getProperty(ELEMENT_PREFIX_KEY + index
											+ ELEMENT_SPAN_LENGTH_KEY)))),
					ConvertUtils.convertToInteger(properties
							.getProperty(ELEMENT_PREFIX_KEY + index
									+ ELEMENT_SPANSET_NUMBEROFSPANS_KEY)),
					ConvertUtils.convertToDouble(properties
							.getProperty(ELEMENT_PREFIX_KEY + index
									+ ELEMENT_SPANSET_A_KEY)), ConvertUtils
							.convertToDouble(properties
									.getProperty(ELEMENT_PREFIX_KEY + index
											+ ELEMENT_SPANSET_B_KEY)));
		} else {
			isPhysicalElement.createSpans(
					new Span(new Fiber(ConvertUtils.convertToDouble(properties
							.getProperty(ELEMENT_PREFIX_KEY + index
									+ ELEMENT_SPAN_LOSSCOEFFICIENT_KEY)),
							ConvertUtils.convertToDouble(properties
									.getProperty(ELEMENT_PREFIX_KEY + index
											+ ELEMENT_SPAN_LENGTH_KEY)))),
					ConvertUtils.convertToDouble(properties
							.getProperty(ELEMENT_PREFIX_KEY + index
									+ ELEMENT_SPAN_TOTAL_LENGTH_KEY)),
					ConvertUtils.convertToDouble(properties
							.getProperty(ELEMENT_PREFIX_KEY + index
									+ ELEMENT_SPANSET_A_KEY)), ConvertUtils
							.convertToDouble(properties
									.getProperty(ELEMENT_PREFIX_KEY + index
											+ ELEMENT_SPANSET_B_KEY)));
		}
		isPhysicalElement.setNoiseFigure(ConvertUtils
				.convertToDouble(properties.getProperty(ELEMENT_PREFIX_KEY
						+ index + ELEMENT_LINEAMPLIFIER_NOISEFIGURE_KEY)));
	}

	private static void setLaserProperties(int index, Laser isPhysicalElement,
			Properties properties) {
		isPhysicalElement.setG(MathUtils.convertdBmToLinear(ConvertUtils
				.convertToDouble(properties.getProperty(ELEMENT_PREFIX_KEY
						+ index + ELEMENT_LASER_G_KEY))));
		isPhysicalElement.setOSNR(ConvertUtils.convertToDouble(properties
				.getProperty(ELEMENT_PREFIX_KEY + index
						+ ELEMENT_LASER_OSNR_KEY)));

	}

	private static void setFiberProperties(int index, Fiber isPhysicalElement,
			Properties properties) {
		isPhysicalElement.setAlfadB(ConvertUtils.convertToDouble(properties
				.getProperty(ELEMENT_PREFIX_KEY + index
						+ ELEMENT_SPAN_LOSSCOEFFICIENT_KEY)));
		isPhysicalElement.setLs(ConvertUtils.convertToDouble(properties
				.getProperty(ELEMENT_PREFIX_KEY + index
						+ ELEMENT_SPAN_LENGTH_KEY)));
	}

	private static void setNodeProperties(int index, Node isPhysicalElement,
			Properties properties) {
		isPhysicalElement
				.setGdB(ConvertUtils.convertToDouble(properties
						.getProperty(ELEMENT_PREFIX_KEY + index
								+ ELEMENT_G_SUFFIX_KEY)));
		isPhysicalElement.setIndex(properties.getProperty(ELEMENT_PREFIX_KEY
				+ index + ELEMENT_INDEX_SUFFIX_KEY));
	}

	private static void setLineAmplifierProperties(int index,
			LineAmplifier isPhysicalElement, Properties properties) {
		isPhysicalElement.setGdB(ConvertUtils.convertToDouble(properties
				.getProperty(ELEMENT_PREFIX_KEY + index
						+ ELEMENT_LINEAMPLIFIER_G_KEY)));
		isPhysicalElement.setNoiseFigure(ConvertUtils
				.convertToDouble(properties.getProperty(ELEMENT_PREFIX_KEY
						+ index + ELEMENT_LINEAMPLIFIER_NOISEFIGURE_KEY)));
	}

	private static void setSpanProperties(int index, Span isPhysicalElement,
			Properties properties) {
		isPhysicalElement.setFiber(new Fiber(ConvertUtils
				.convertToDouble(properties.getProperty(ELEMENT_PREFIX_KEY
						+ index + ELEMENT_SPAN_LOSSCOEFFICIENT_KEY)),
				ConvertUtils.convertToDouble(properties
						.getProperty(ELEMENT_PREFIX_KEY + index
								+ ELEMENT_SPAN_LENGTH_KEY))));
		isPhysicalElement.setNoiseFigure(ConvertUtils
				.convertToDouble(properties.getProperty(ELEMENT_PREFIX_KEY
						+ index + ELEMENT_LINEAMPLIFIER_NOISEFIGURE_KEY)));
	}

	private static void setCompensatedNodeProperties(int index,
			CompensatedNode compensatedNode, Properties properties) {
		compensatedNode
				.setGdB(ConvertUtils.convertToDouble(properties
						.getProperty(ELEMENT_PREFIX_KEY + index
								+ ELEMENT_G_SUFFIX_KEY)));
	}

	private static IsPhysicalElement createElement(int index,
			Properties properties) {
		IsPhysicalElement object = null;
		int type = ConvertUtils.convertToInteger(properties
				.getProperty(ELEMENT_PREFIX_KEY + index
						+ ELEMENT_TYPE_SUFFIX_KEY));
		switch (type) {
		case 0:
			object = new Node();
			break;
		case 1:
			object = new CompensatedNode();
			break;
		case 2:
			object = new Fiber();
			break;
		case 3:
			object = new LineAmplifier();
			break;
		case 4:
			object = new Span();
			break;
		case 5:
			object = new Laser();
			break;
		case 6:
			object = new SpanSet();
			break;
		default:
			break;
		}
		return object;
	}
}
