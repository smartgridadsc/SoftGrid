package org.openmuc.j60870;

import org.openmuc.j60870.job.Job;
import org.openmuc.j60870.job.MessageContext;

public class AsduWorker implements Runnable {
    private MessageContext messageContext;
    private Job job;

    public AsduWorker(MessageContext messageContext, Job job) {
        this.messageContext = messageContext;
        this.job = job;
    }

    @Override
    public void run() {

        if (messageContext != null &&
                messageContext.getConnection() != null &&
                messageContext.getaSdu() != null) {
            job.execute(messageContext);


        }
    }
}
