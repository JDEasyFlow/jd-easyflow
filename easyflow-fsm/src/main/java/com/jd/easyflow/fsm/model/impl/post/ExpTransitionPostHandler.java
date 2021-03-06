package com.jd.easyflow.fsm.model.impl.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.easyflow.fsm.FsmContext;
import com.jd.easyflow.fsm.el.ElFactory;
import com.jd.easyflow.fsm.model.TransitionContext;
import com.jd.easyflow.fsm.model.TransitionPostHandler;

/**
 * 
 * @author liyuliang5
 *
 */
public class ExpTransitionPostHandler extends AbstractTransitionPostHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExpTransitionPostHandler.class);

	private String exp;

	public ExpTransitionPostHandler() {
	}

	public ExpTransitionPostHandler(String exp) {
		this.exp = exp;
	}

	@Override
	public String postHandle(TransitionContext transitionContext, FsmContext context) {
		logger.info("EVAL SPEL:" + exp);
		String result = ElFactory.get().eval(exp, transitionContext, context, null);
		logger.info("SPEL RESULT:" + result);
		return result;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

}
