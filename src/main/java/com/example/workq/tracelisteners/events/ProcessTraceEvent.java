package com.example.workq.tracelisteners.events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.example.workq.tracelisteners.model.Process;

/**
 * This class contains the data model for an process event.
 */
@JsonPropertyOrder({"ProcessInstanceId", "TimeStamp", "EventType", "EventActionType", "Process"})
public class ProcessTraceEvent extends TraceEvent {

    private EventActionType eventActionType = EventActionType.None;
    private Process process;

    @JsonProperty("EventActionType")
    public EventActionType getEventActionType() {
        return this.eventActionType;
    }

    public void setEventActionType(EventActionType eventActionType) {
        this.eventActionType = eventActionType;
    }

    @JsonProperty("Process")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public String toString() {
        return "ProcessTraceEvent{" +
            "ProcessInstanceId=" + getProcessInstanceId() +
            ", timestamp=" + getTimeStamp() +
            ", traceEventType=" + getTraceEventType() +
            ", eventActionType=" + eventActionType +
            ", process=" + process +
            '}';
    }
}
