package com.example.workq.tracelisteners.messaging;

import com.example.workq.tracelisteners.events.ProcessTraceEvent;
import com.example.workq.tracelisteners.events.SlaViolatedTraceEvent;
import com.example.workq.tracelisteners.events.TaskTraceEvent;


/**
 * Provides the API for publishing a {@link com.workq.tracelisteners.events.TraceEvent} to some message broker asynchronously
 */
public interface MessagePublisher {

    void publishMessage(ProcessTraceEvent event) throws PublishingFailedException;

    void publishMessage(SlaViolatedTraceEvent event) throws PublishingFailedException;

    void publishMessage(TaskTraceEvent event) throws PublishingFailedException;

}
