package com.jd.easyflow.flow.engine.impl;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.easyflow.flow.engine.FlowContext;
import com.jd.easyflow.flow.engine.FlowRunner;
import com.jd.easyflow.flow.filter.FilterChain;
import com.jd.easyflow.flow.model.Flow;
import com.jd.easyflow.flow.model.FlowNode;
import com.jd.easyflow.flow.model.NodeContext;
import com.jd.easyflow.flow.util.FlowConstants;
import com.jd.easyflow.flow.util.FlowEventTypes;

/**
 * 
 * @author liyuliang5
 *
 */
public abstract class BaseFlowRunner implements FlowRunner {

    private static final Logger logger = LoggerFactory.getLogger(BaseFlowRunner.class);

    @Override
    public void run(FlowContext context) {
        Flow flow = context.getFlow();
        flow.triggerEvent(FlowEventTypes.RUN_START, context);
        doRun(context);
        flow.triggerEvent(FlowEventTypes.RUN_END, context);
    }

    /**
     * Run flow.
     * 
     * @param context
     */
    public abstract void doRun(FlowContext context);

    /**
     * Run one node.
     * 
     * @param currentNode
     * @param context
     * @param flow
     */
    protected void runOneNode(NodeContext currentNode, FlowContext context, Flow flow) {
        if (logger.isInfoEnabled()) {
            logger.info("EXECUTE NODE:" + currentNode.getNodeId());
        }
        FlowNode node = context.getFlow().getNode(currentNode.getNodeId());
        NodeContext[] nextNodes = null;
        try {
            runNode(node, currentNode, context, flow);
            // get next nodes
            nextNodes = currentNode.getNextNodes();
        } catch (Throwable t) { // NOSONAR
            currentNode.setThrowable(t);
            throw t;
        } finally {
            if (nextNodes != null) {
                context.addNodes(nextNodes);
            } else {
                context.addEndNode(currentNode);
            }
        }
        // print nodes info
        if (logger.isInfoEnabled()) {
            StringBuilder builder = new StringBuilder();
            if (nextNodes != null) {
                for (NodeContext n : nextNodes) {
                    builder.append(n.getNodeId() + ",");
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("NEXT NODES:" + (builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1)));
            }
        }
        // Clear previous node to avoid OOM
        if (Boolean.FALSE.equals(flow.getProperty(FlowConstants.FLOW_PROPERTY_RECORD_HISTORY))) {
            currentNode.setPreviousNode(null);
            currentNode.setNextNodes(null);
        }
    }

    protected void runNode(FlowNode node, NodeContext currentNode, FlowContext context, Flow flow) {
        if (flow.getNodeFilters() == null || flow.getNodeFilters().size() == 0) {
            invokeNode(node, currentNode, context, flow);
            return;
        }
        FilterChain<Triple<FlowNode, NodeContext, FlowContext>, NodeContext> chain = new FilterChain<Triple<FlowNode, NodeContext, FlowContext>, NodeContext>(
                flow.getNodeFilters(), p -> {
                    return invokeNode(node, currentNode, context, flow);
                });
        chain.doFilter(Triple.of(node, currentNode, context));

    }

    private NodeContext invokeNode(FlowNode node, NodeContext currentNode, FlowContext context, Flow flow) {
        Throwable throwable = null;
        try {
            flow.triggerEvent(FlowEventTypes.NODE_START, currentNode, context, false);
            // Execute node
            NodeContext nodeContext = node.execute(currentNode, context);
            flow.triggerEvent(FlowEventTypes.NODE_END, currentNode, context, false);
            return nodeContext;
        } catch (Throwable t) {// NOSONAR
            throwable = t;
            logger.error("Flow node execute exception, Node:" + currentNode.getNodeId() + "," + t.getMessage());
            throw t;
        } finally {
            currentNode.setThrowable(throwable);
            flow.triggerEvent(FlowEventTypes.NODE_COMPLETE, currentNode, context, true);
        }
    }
}
