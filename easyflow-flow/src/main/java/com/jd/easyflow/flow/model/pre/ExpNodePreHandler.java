package com.jd.easyflow.flow.model.pre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.easyflow.flow.el.ElFactory;
import com.jd.easyflow.flow.engine.FlowContext;
import com.jd.easyflow.flow.model.NodeContext;
import com.jd.easyflow.flow.model.NodePreHandler;
import com.jd.easyflow.flow.model.post.ExpNodePostHandler;

/**
 * 
 * @author liyuliang5
 *
 */
public class ExpNodePreHandler implements NodePreHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExpNodePostHandler.class);

	private String exp;

	public ExpNodePreHandler() {
	}

	public ExpNodePreHandler(String exp) {
		this.exp = exp;
	}

	@Override
	public boolean preHandle(NodeContext nodeContext, FlowContext context) {
		boolean result = ElFactory.get().eval(exp, nodeContext, context, null);
		if (logger.isInfoEnabled()) {
		    logger.info("Exp:" + exp + " result:" + result);
		}
		return result;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

}
