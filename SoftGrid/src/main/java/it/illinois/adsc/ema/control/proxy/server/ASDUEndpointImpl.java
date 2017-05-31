//package it.illinois.adsc.ema.control.proxy.server;
//
//import it.illinois.adsc.ema.interceptor.ASduEndpoint;
//import org.openmuc.j60870.ASdu;
//import org.openmuc.j60870.CauseOfTransmission;
//
//import java.io.EOFException;
//import java.io.IOException;
//
///**
// * Created by prageeth.g on 29/5/2017.
// */
//public class ASDUEndpointImpl implements ASduEndpoint {
//
//    @Override
//    public void validForExecution(ASdu aSdu) {
//
//        try {
//            //running interceptor in ASdu before passing it into IED server
//
//            switch (aSdu.getTypeIdentification()) {
//                //  interrogation command
//                //  There are two type of interrorgation commands
//                //  substation system interrogation and group interrogation
//                //  01. substation system interrogation : to get the complete data set of the substation
//                //  02. group interrogation : to get the specific data set of the substation
//                case C_IC_NA_1:
//                    sendToCC(aSdu, true);
//                    logEvent("Got Interrogation Command. Will send circuit breaker status value.\n");
//                    sendInterrogationResults(aSdu);
//                    break;
//                case C_SC_NA_1:
//                    sendToCC(aSdu, true);
//                    logEvent("Single Command Received.\n");
//                    hanldeSingleCommand(aSdu);
//                    break;
//                case C_SE_NC_1:
//                    sendToCC(aSdu, true);
//                    logEvent("Single Command Received.\n");
//                    handleSetShortFloatCommand(aSdu);
//                    break;
//                case C_RP_NA_1:
//                    sendToCC(aSdu, true);
//                    logEvent("Reset Process Command.\n");
//                    handleResetProcessCommand(aSdu);
//                    break;
//                default:
//                    logEvent("Got unknown request: " + aSdu + ". Will not confirm it.\n");
//            }
//        } catch (EOFException e) {
//            logEvent("Will quit listening for commands on connection (" + connectionId + ") because socket was closed.");
//        } catch (IOException e) {
//            logEvent("Will quit listening for commands on connection (" + connectionId + ") because of error: \"" + e.getMessage() + "\".");
//        }
//    }
//
//    private void sendToCC(ASdu asduToCC, boolean confirmation) {
//        if (confirmation) {
//            ASdu aSdu = asduToCC;
//            CauseOfTransmission cot = aSdu.getCauseOfTransmission();
//            if (cot == CauseOfTransmission.ACTIVATION) {
//                cot = CauseOfTransmission.ACTIVATION_CON;
//            } else if (cot == CauseOfTransmission.DEACTIVATION) {
//                cot = CauseOfTransmission.DEACTIVATION_CON;
//            }
//            asduToCC = new ASdu(aSdu.getTypeIdentification(), aSdu.isSequenceOfElements(), cot, aSdu.isTestFrame(),
//                    aSdu.isNegativeConfirm(), aSdu.getOriginatorAddress(), aSdu.getCommonAddress(),
//                    aSdu.getInformationObjects());
//        }
//        try {
//            if (connection == null) {
//                iec60870104ConnectionWrapper.send(asduToCC);
//                logEvent("DELIVERED iec60870104ConnectionWrapper = " + asduToCC);
//            } else {
//                connection.send(asduToCC);
//                logEvent("DELIVERED asduToCC = " + asduToCC);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
