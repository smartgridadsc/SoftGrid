package org.openmuc.j60870.job;

import org.openmuc.j60870.loger.PRXMessageCounter;

import java.io.IOException;

/**
 * Created by prageethmahendra on 19/7/2016.
 */
public class ResponseJob implements Job {

    @Override
    public void execute(MessageContext messageContext) {
        try {
            messageContext.getConnection().send(messageContext.getaSdu());
            PRXMessageCounter.logMessage(PRXMessageCounter.SEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
