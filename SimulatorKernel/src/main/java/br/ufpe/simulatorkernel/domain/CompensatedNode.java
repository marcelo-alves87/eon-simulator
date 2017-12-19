package br.ufpe.simulatorkernel.domain;

import org.apache.log4j.Logger;

import br.ufpe.simulator.messages.MessageUtils;

public class CompensatedNode extends IsPhysicalElement {

	private static Logger logger = Logger.getLogger(CompensatedNode.class);

	private class NodeLineAmplifier extends LineAmplifier {

		@Override
		public double getG() {
			return 1 / node.getG();
		}

	}

	private static final String NODE_INFO_G_SET = "compensatedNode.gSet.info";
	private static final String NODE_INFO_G_RESULT = "compensatedNode.gResult.info";
	private Node node;
	private NodeLineAmplifier nodeLineAmplifier;

	public CompensatedNode() {
		this.node = new Node();
		this.nodeLineAmplifier = new NodeLineAmplifier();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public NodeLineAmplifier getNodeLineAmplifier() {
		return nodeLineAmplifier;
	}

	public void setNodeLineAmplifier(NodeLineAmplifier nodeLineAmplifier) {
		this.nodeLineAmplifier = nodeLineAmplifier;
	}

	@Override
	public double getG() {
		double d = node.getG();
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(NODE_INFO_G_RESULT,
					getIndex(), d));
		}
		return d * nodeLineAmplifier.getG();
	}

	@Override
	public void setGdB(double g) {
		if (logger.isInfoEnabled()) {
			logger.info(MessageUtils.createMessage(NODE_INFO_G_SET, getIndex(),
					g));
		}
		node.setGdB(g);
	}

	@Override
	public double getLinearNoise() {
		return nodeLineAmplifier.getLinearNoise();
	}

	@Override
	public boolean isTopologyNode() {
		return true;
	}

}
