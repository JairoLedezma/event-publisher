package com.example.listeners;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleTraceEventListener implements AgendaEventListener {
	protected static final Logger LOGGER = LoggerFactory.getLogger(RuleTraceEventListener.class);

    public void afterMatchFired(AfterMatchFiredEvent event) {
		LOGGER.info("afterMatchFired: " + event.toString());
	}

	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		LOGGER.info("beforeRuleFlowGroupActivated: " + event.toString());
	}

	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
		LOGGER.info("afterRuleFlowGroupActivated: " + event.toString());
	}

	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
		LOGGER.info("agendaGroupPopped: " + event.toString());
	}

	public void agendaGroupPushed(AgendaGroupPushedEvent event) {
		LOGGER.info("agendaGroupPushed: " + event.toString());
	}

	public void matchCreated(MatchCreatedEvent event) {
		LOGGER.info("matchCreated: " + event.toString());
	}

	public void matchCancelled(MatchCancelledEvent event) {
		LOGGER.info("matchCancelled: " + event.toString());
	}

	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		LOGGER.info("beforeMatchFired: " + event.toString());
	}

	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		LOGGER.info("beforeRuleFlowGroupDeactivated: " + event.toString());
	}

	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		LOGGER.info("afterRuleFlowGroupDeactivated: " + event.toString());
		
		// send rules not fired to queue for each rule flow group
		//transformRulesNotFired();
	}
}
