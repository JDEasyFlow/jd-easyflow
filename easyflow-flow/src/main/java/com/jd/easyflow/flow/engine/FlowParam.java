package com.jd.easyflow.flow.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author liyuliang5
 *
 */
public class FlowParam {
    
    public FlowParam() {
        // NOOP
    }
    
    public FlowParam(String flowId) {
        this.flowId = flowId;
    }
    
    public FlowParam(String flowId, Object param) {
        this.flowId = flowId;
        this.param = param;
    }
    
    public FlowParam(String flowId, String[] nodeIds, Object param) {
        this.flowId = flowId;
        this.nodeIds = nodeIds;
        this.param = param;
    }
    
    public FlowParam(String flowId, String[] nodeIds, Object param, Map<String, Object> dataMap) {
        this.flowId = flowId;
        this.nodeIds = nodeIds;
        this.param = param;
        this.dataMap = dataMap;
    }
    
    public FlowParam(String flowId, String nodeId, Object param) {
        this.flowId = flowId;
        this.nodeIds = new String[] {nodeId};
        this.param = param;
    }

    private String flowId;

    private String[] nodeIds;
    /**
     * business param.
     */
    private Object param;

    /**
     * Flow context.
     */
    @JsonIgnore
    private FlowContext context;
    /**
     * common param data.
     */
    private Map<String, Object> dataMap;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String[] getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(String[] nodeIds) {
        this.nodeIds = nodeIds;
    }

    public void setNodeId(String nodeId) {
        if (nodeId == null) {
            return;
        }
        this.nodeIds = new String[] { nodeId };
    }

    public <T> T getParam() {
        return (T) param;
    }

    /**
     * param should be Object[] or List
     * 
     * @param <T>
     * @param index
     * @return
     */
    public <T> T getParam(int index) {
        if (param == null) {
            return null;
        }
        if (param instanceof Object[]) {
            return (T) ((Object[]) param)[index];
        } else if (param instanceof List) {
            return (T) ((List) param).get(index);
        }
        throw new IllegalStateException("Param:" + param + " is not index type");
    }

    /**
     * param should be Map
     * 
     * @param <T>
     * @param key
     * @return
     */
    public <T> T getParam(String key) {
        if (param == null) {
            return null;
        }
        return (T) ((Map<String, Object>) param).get(key);
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public FlowContext getContext() {
        return context;
    }

    public void setContext(FlowContext context) {
        this.context = context;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void seDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public void put(String key, Object value) {
        if (dataMap == null) {
            dataMap = new ConcurrentHashMap<>();
        }
        dataMap.put(key, value);
    }

    public <T> T get(String key) {
        if (dataMap == null) {
            return null;
        }
        return (T) dataMap.get(key);
    }
    
}
