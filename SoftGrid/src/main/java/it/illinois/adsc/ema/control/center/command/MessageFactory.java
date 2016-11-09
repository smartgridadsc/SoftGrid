package it.illinois.adsc.ema.control.center.command;

import it.illinois.adsc.ema.control.center.CCTimeLoger;
import it.illinois.adsc.ema.control.center.ControlCenterClient;
import it.illinois.adsc.ema.control.center.ControlCenterClient_Attacker;
import it.illinois.adsc.ema.control.center.ControlCenterContext;
import it.illinois.adsc.ema.control.center.experiments.CCMessageCounter;
import it.illinois.adsc.ema.control.ied.pw.IEDServerFactory;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.ui.ControlCenterGUI;
import org.openmuc.j60870.*;

import java.io.IOException;

/**
 * Created by prageethmahendra on 18/2/2016.
 */
public class MessageFactory {
    public static ASdu sendCommand(Command command, Connection clientConnection, ControlCenterGUI controlCenterGUI,  CCMessageCounter ccMessageCounter) throws IOException {
        return sendCommand(command, clientConnection, controlCenterGUI, false, ccMessageCounter);
    }

    public static ASdu sendCommand(Command command, Connection clientConnection, ControlCenterGUI controlCenterGUI, boolean attack, CCMessageCounter ccMessageCounter) throws IOException {
        ASdu aSdu = null;
        switch (command.getCommandType()) {
            case INTERROGATION:
                CCTimeLoger.resetStartTime("interrogation");
//                clientConnection.interrogation(command.getSubtationAddressSpace(),
//                        CauseOfTransmission.ACTIVATION,
//                        new IeQualifierOfInterrogation(20));
                aSdu = new ASdu(TypeId.C_IC_NA_1, false, CauseOfTransmission.ACTIVATION, false, false,
                        clientConnection.getOriginatorAddress(), command.getSubtationAddressSpace(),
                        new InformationObject[]{new InformationObject(0,
                                new InformationElement[][]{{new IeQualifierOfInterrogation(20)}})});
                break;
            case SINGLE_COMMAND:
                CCTimeLoger.resetStartTime("singleCommand");
//                clientConnection.singleCommand(command.getSubtationAddressSpace(),
//                        command.getObjectAddress(),
//                        new IeSingleCommand(getValue(command), command.getFeildQualifier(), true));
                CauseOfTransmission cot;
                IeSingleCommand singleCommand = new IeSingleCommand(getValue(command), command.getFeildQualifier(), true);
                if (singleCommand.isCommandStateOn()) {
                    cot = CauseOfTransmission.ACTIVATION;
                } else {
                    cot = CauseOfTransmission.DEACTIVATION;
                }
                aSdu = new ASdu(TypeId.C_SC_NA_1, false, cot, false, false, clientConnection.getOriginatorAddress(),
                        command.getSubtationAddressSpace(),
                        new InformationObject[]{new InformationObject(command.getObjectAddress(),
                                new InformationElement[][]{{singleCommand}})});
                break;
            case SET_SHORT_FLOAT:
                CCTimeLoger.resetStartTime("set short float command");
//                clientConnection.setShortFloatCommand(command.getSubtationAddressSpace(),
//                        CauseOfTransmission.REQUEST, command.getObjectAddress(),
//                        new IeShortFloat(Float.parseFloat(command.getValue())),
//                        new IeQualifierOfSetPointCommand(command.getFeildQualifier(), true));
                aSdu = new ASdu(TypeId.C_SE_NC_1, false, CauseOfTransmission.REQUEST, false, false,
                        clientConnection.getOriginatorAddress(), command.getSubtationAddressSpace(),
                        new InformationObject[]{new InformationObject(command.getObjectAddress(),
                                new InformationElement[][]{{new IeShortFloat(Float.parseFloat(command.getValue())),
                                        new IeQualifierOfSetPointCommand(command.getFeildQualifier(), true)}})});
                break;
            case ATTACK:
                CCTimeLoger.resetStartTime("executeWithDelay Attack Command");
                try {
                    ControlCenterClient_Attacker.getInstance(new ControlCenterContext(ConfigUtil.CONFIG_PEROPERTY_FILE)).setControlCenterGUI(controlCenterGUI);
                    ControlCenterClient_Attacker.getInstance(null).startClient(
                            IEDServerFactory.proxyIpPorts,
                            command.getIEDType(),
                            command.getFeild(),
                            command.getValue(),
                            command.getPercent());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case CANCEL:
                CCTimeLoger.resetStartTime("Cancel All the Commands in the queue");
                try {
//                    clientConnection.resetProcessCommand(command.getSubtationAddressSpace(),
//                            new IeQualifierOfResetProcessCommand(command.getSubtationAddressSpace()));
                    aSdu = new ASdu(TypeId.C_RP_NA_1, false, CauseOfTransmission.ACTIVATION, false, false,
                            clientConnection.getOriginatorAddress(),
                            command.getSubtationAddressSpace(), new InformationObject[]{new InformationObject(0,
                            new InformationElement[][]{{new IeQualifierOfResetProcessCommand(
                                    command.getSubtationAddressSpace())}})});

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("command not implemented = " + command.getCommandType());
        }
        if (aSdu != null) {
            if(ccMessageCounter != null)
            {
                ccMessageCounter.logMessageReceived(aSdu);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Message Sent without a ccMessage Counter ...!");
            }
            System.out.println("Sent : " + aSdu);
            if (ControlCenterClient.getCCSecurityHandler() != null) {
//              ControlCenterClient.getCCSecurityHandler().commandSent(aSdu);
            }

            clientConnection.send(aSdu);

        }
        if (attack) {
            return null;
        } else {
            return aSdu;
        }
    }

    private static boolean getValue(Command command) {
        if (command.getValue().equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }
}
