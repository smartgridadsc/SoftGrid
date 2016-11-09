//package org.openmuc.j60870.job;
//
//import it.illinois.adsc.ema.control.proxy.server.SecurityHandler;
//import org.openmuc.j60870.TypeId;
//
///**
// * Created by prageethmahendra on 18/7/2016.
// */
//public class TranslatorJob implements Job {
//
//    @Override
//    public void execute(MessageContext messageContext) {
//        if (messageContext.getaSdu().getTypeId().equals(TypeId.C_RP_NA_1)) {
//            System.out.println(">>>>>>> Reset Process Command Detected....!");
//            SecurityHandler.getInstance().resetAll();
//        }
//
//        SecurityHandler.getInstance().validateAndExecute(messageContext.getaSdu());
//
//    }
//}
