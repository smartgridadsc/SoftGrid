package it.illinois.adsc.ema.control.center.security;

import it.illinois.adsc.ema.control.center.ControlCenterClient;
import it.illinois.adsc.ema.control.center.command.Command;
import it.illinois.adsc.ema.control.center.command.CommandParser;
import it.illinois.adsc.ema.control.center.command.MessageFactory;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;

import java.io.IOException;
import java.util.*;

/**
 * Created by prageethmahendra on 19/5/2016.
 */
public class CCSecurityHandler implements CommandListener {
    private static CCSecurityHandler instance;
    private HashMap<ASdu, Long> timeAsduMap = new HashMap<ASdu, Long>();
    private static boolean enable = true;

    private CCSecurityHandler() {
    }

    public static CCSecurityHandler getInstance() {
        if (instance == null) {
            instance = new CCSecurityHandler();
        }
        return instance;
    }

    private boolean isCriticalCommand(ASdu aSdu) {
        boolean criticalCommand = false;
        switch (aSdu.getTypeIdentification()) {
            case C_SC_NA_1:
                criticalCommand = true;
                break;
            default:
                criticalCommand = false;
        }
        return criticalCommand;
    }

    public void newASdu(ASdu aSdu, long nanoTime) {
//        System.out.println(this.getClass().toString() + " : Received ASDU: " + aSdu.toString().replace('\n',','));
        Iterator<ASdu> aSduIterator = timeAsduMap.keySet().iterator();
        boolean found = false;
        while (aSduIterator.hasNext()) {
            if (aSduIterator.next().isConfirmation(aSdu)) {
                aSduIterator.remove();
                found = true;
            }
        }
        if(!ControlCenterClient.manualExperimentMode) {
            if (!found && aSdu.getCauseOfTransmission().equals(CauseOfTransmission.ACTIVATION_CON) && isCriticalCommand(aSdu)) {
                Command command = CommandParser.parseCommandString("Cancel All");
//            System.out.println("Cancel Items ...!");
                for (String clientAddress : ControlCenterClient.PROXY_CONNECTION_MAP.keySet()) {
                    try {
                        MessageFactory.sendCommand(command, ControlCenterClient.PROXY_CONNECTION_MAP.get(clientAddress), null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addToSentCommands(ASdu aSdu) {
        if (isCriticalCommand(aSdu)) {
            System.out.println(aSdu);
            timeAsduMap.put(aSdu, System.nanoTime());
        }
    }

    @Override
    public void commandSent(ASdu aSdu) {
        addToSentCommands(aSdu);
    }

    private boolean isInvalid(ASdu aSdu) {
        return timeAsduMap.size() > 3 && enable;
    }

    public static void setEnable(boolean enable) {
        CCSecurityHandler.enable = enable;
    }
}
