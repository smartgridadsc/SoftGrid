package org.openmuc.j60870.job;

import org.openmuc.j60870.loger.PRXMessageCounter;

import java.io.IOException;

/**
 * Created by prageethmahendra on 18/7/2016.
 */
public class ConfirmationJob implements Job {

    @Override
    public void execute(MessageContext messageContext) {
//        try {
        PRXMessageCounter.logMessage(PRXMessageCounter.RECEIVED);
//            if(IEC60870104Server.SECURITY_INBUILT)
//            {
//                messageContext.getConnection().sendConfirmation(messageContext.getaSdu());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
