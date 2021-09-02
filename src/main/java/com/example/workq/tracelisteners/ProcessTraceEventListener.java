package com.example.workq.tracelisteners;

import com.example.workq.tracelisteners.events.EventActionType;
import com.example.workq.tracelisteners.events.ProcessTraceEvent;
import com.example.workq.tracelisteners.events.SlaViolatedTraceEvent;
import com.example.workq.tracelisteners.events.TraceEventType;
import com.example.workq.tracelisteners.messaging.KafkaPublisher;
import com.example.workq.tracelisteners.messaging.MessagePublisher;
import com.example.workq.tracelisteners.messaging.PublishingFailedException;
import com.example.workq.tracelisteners.model.Node;
import com.example.workq.tracelisteners.model.NodeState;
import com.example.workq.tracelisteners.model.Process;
import java.time.LocalDateTime;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This listener is added to the RHPAM Process in {@code META-INF/kie-deployment-descriptors.xml}. This can also be configured in the Business Central
 * UI.
 * <p>
 * When an event occurs in the RHPAM process instance, such as "node triggered", this listener will pull data associated with the event and publish it
 * to an AMQ queue for future analytics purposes.
 */
@SuppressWarnings({"DuplicatedCode", "unused"})
public class ProcessTraceEventListener implements ProcessEventListener {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ProcessTraceEventListener.class);
    private static final MessagePublisher publisher = new KafkaPublisher(); // TODO: Find a nicer way to do this...
    private LocalDateTime nodeStartTime;

    /**
     * Placeholder for future SLA functionality
     *
     */
    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {
        LOGGER.info("AfterSlaViolated: {}", event);
        SlaViolatedTraceEvent traceMessage = new SlaViolatedTraceEvent();
        traceMessage.setTimeStamp(LocalDateTime.now());
        traceMessage.setProcessInstanceId(String.valueOf(event.getProcessInstance().getId()));
        traceMessage.setTraceEventType(TraceEventType.SlaViolatedEvent);
        traceMessage.setNodeId(event.getNodeInstance().getId());
        traceMessage.setResponsibleNodeName(event.getNodeInstance().getNodeName());

        try {
            //LOGGER.info("Sending message {}", traceMessage);
            publisher.publishMessage(traceMessage);
        } catch (PublishingFailedException exception) {
            LOGGER.warn("Failed to publish message", exception);
        }
        
    }
    /**
     * Before some a node starts
     *
     */
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        LOGGER.info("BeforeNodeTriggered: " + event.toString());
        nodeStartTime = LocalDateTime.now();
        String id = Long.toString(event.getProcessInstance().getId());

        RuleFlowProcessInstance rfpi = (RuleFlowProcessInstance) event.getProcessInstance();


        ProcessTraceEvent processTraceEvent = new ProcessTraceEvent();
        processTraceEvent.setTraceEventType(TraceEventType.ProcessTraceEvent);
        processTraceEvent.setTimeStamp(LocalDateTime.now());
        processTraceEvent.setEventActionType(EventActionType.BeforeNodeTriggered);
        processTraceEvent.setProcessInstanceId(id);

        Node node = new Node();
        node.setState(NodeState.Started);
        node.setStartedOn(nodeStartTime);
        node.setCompletedOn(null);
//        node.setID(Long.toString(event.getNodeInstance().getId()));
        node.setName(event.getNodeInstance().getNodeName());


        Process process = new Process();
        process.setNode(node);
        process.setName(event.getProcessInstance().getProcessName());
        process.setProcessVariables(rfpi.getVariables());

        processTraceEvent.setProcess(process);

        try {
            //LOGGER.info("Sending message {}", processTraceEvent);
            publisher.publishMessage(processTraceEvent);
        } catch (PublishingFailedException e) {
            LOGGER.warn("Failed to publish message", e);
        }
        
    }
    /**
     * Before a process starts
     *
     */
    public void beforeProcessStarted(ProcessStartedEvent event) {
        LOGGER.info("BeforeProcessStarted: " + event.toString());

        RuleFlowProcessInstance rfpi = (RuleFlowProcessInstance) event.getProcessInstance();

        String id = Long.toString(rfpi.getId());

        ProcessTraceEvent processTraceEvent = new ProcessTraceEvent();
        processTraceEvent.setTraceEventType(TraceEventType.ProcessTraceEvent);
        processTraceEvent.setTimeStamp(LocalDateTime.now());
        processTraceEvent.setEventActionType(EventActionType.BeforeProcessStarted);
        processTraceEvent.setProcessInstanceId(id);
        Process process = new Process();
        process.setName(event.getProcessInstance().getProcessName());
//        process.setProcessVariables(rfpi.getVariables());

        processTraceEvent.setProcess(process);

        try {
            //LOGGER.info("BeforeProcessStarted sending to queue");
            publisher.publishMessage(processTraceEvent);
        } catch (PublishingFailedException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * After the process has completed.
     *
     */
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.info("AfterProcessCompleted: " + event.toString());
        if(event.getProcessInstance().getParentProcessInstanceId() == -1 ) {
            sendProcessCompletedEvent(event);

            // close the publisher when everything is done
            try {
                LOGGER.info("Closing process listener publisher");
            } catch (Exception e) {
                LOGGER.warn("Failed to close publisher", e);
            }
        }
    }

    /**
     * Logs the be
     *
     */
    private void sendProcessCompletedEvent(ProcessCompletedEvent event) {
        RuleFlowProcessInstance rfpi = (RuleFlowProcessInstance) event.getProcessInstance();

        String id = Long.toString(rfpi.getId());

        // Send process completed event
        ProcessTraceEvent processTraceEvent = new ProcessTraceEvent();
        processTraceEvent.setTraceEventType(TraceEventType.ProcessTraceEvent);
        processTraceEvent.setTimeStamp(LocalDateTime.now());
        processTraceEvent.setEventActionType(EventActionType.AfterProcessCompleted);
        processTraceEvent.setProcessInstanceId(id);
        Process process = new Process();
        process.setName(event.getProcessInstance().getProcessName());
        processTraceEvent.setProcess(process);


        try {
           // LOGGER.info("BeforeProcessStarted sending to queue");
            publisher.publishMessage(processTraceEvent);
        } catch (PublishingFailedException e) {
            LOGGER.warn("Failed to publish message", e);

        }
        
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.trace("BeforeProcessCompleted: " + event.toString());
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        LOGGER.info("AfterNodeLeft: " + event.toString());

        RuleFlowProcessInstance rfpi = (RuleFlowProcessInstance) event.getProcessInstance();

        String id = Long.toString(event.getProcessInstance().getId());

        ProcessTraceEvent processTraceEvent = new ProcessTraceEvent();
        processTraceEvent.setTraceEventType(TraceEventType.ProcessTraceEvent);
        processTraceEvent.setTimeStamp(LocalDateTime.now());
        processTraceEvent.setEventActionType(EventActionType.AfterNodeLeft);
        processTraceEvent.setProcessInstanceId(id);

        Node node = new Node();
        node.setState(NodeState.Completed);
        node.setStartedOn(nodeStartTime);
        node.setCompletedOn(LocalDateTime.now());
//        node.setID(Long.toString(event.getNodeInstance().getId()));
        node.setName(event.getNodeInstance().getNodeName());

        Process process = new Process();
        process.setNode(node);
        process.setName(event.getProcessInstance().getProcessName());
        process.setProcessVariables(rfpi.getVariables());

        processTraceEvent.setProcess(process);

        try {
            publisher.publishMessage(processTraceEvent);
        } catch (PublishingFailedException e) {
            LOGGER.warn("Failed to publish message", e);
        }
        
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        LOGGER.trace("AfterNodeTriggered: " + event.toString());
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        LOGGER.trace("AfterProcessStarted: " + event.toString());
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        LOGGER.trace("AfterVariableChanged: " + event.toString());
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        LOGGER.trace("BeforeNodeLeft: " + event.toString());
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        LOGGER.trace("BeforeVariableChanged: " + event.toString());
    }

}