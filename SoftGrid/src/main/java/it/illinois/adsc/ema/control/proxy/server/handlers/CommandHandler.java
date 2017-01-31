package it.illinois.adsc.ema.control.proxy.server.handlers;

import it.illinois.adsc.ema.control.LogEventListener;
import it.illinois.adsc.ema.control.proxy.context.ProxyServerContext;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;

import java.io.IOException;
import java.util.List;

/**
 * Created by prageethmahendra on 16/1/2017.
 */
public abstract class CommandHandler implements ICommandHandler {
    protected static TypeId typeid;
    protected ProxyServerContext proxyContext;
    private static LogEventListener logEventListener;
    private static CCConnection connection;

    public abstract void handle(ASdu aSdu);

    private void deliver(ASdu aSdu, List<InformationObject> informationObjectList, TypeId typeId) throws Exception {
        InformationObject[] informationObjects = null;
        if (informationObjectList != null) {
            informationObjects = new InformationObject[informationObjectList.size()];
            int i = 0;
            for (InformationObject informationObject : informationObjectList) {
                informationObjects[i] = informationObject;
                i++;
            }
        }
        ASdu resultAsdu = new ASdu(typeId, true, CauseOfTransmission.SPONTANEOUS, false, false, 0, aSdu.getCommonAddress(), informationObjects);
        logEvent("resultAsdu = " + resultAsdu);
        sendToCC(resultAsdu, false);
    }

    private void logEvent(String logString) {
        if (logEventListener != null) {
            logEventListener.logEvent(logString);
        }
    }

    private void sendToCC(ASdu asduToCC, boolean confirmation) throws Exception {
        if (connection == null) {
            throw new Exception("No Connection Exception...!");
        }
        if (confirmation) {
            ASdu aSdu = asduToCC;
            CauseOfTransmission cot = aSdu.getCauseOfTransmission();
            if (cot == CauseOfTransmission.ACTIVATION) {
                cot = CauseOfTransmission.ACTIVATION_CON;
            } else if (cot == CauseOfTransmission.DEACTIVATION) {
                cot = CauseOfTransmission.DEACTIVATION_CON;
            }
            asduToCC = new ASdu(aSdu.getTypeIdentification(), aSdu.isSequenceOfElements(), cot, aSdu.isTestFrame(),
                    aSdu.isNegativeConfirm(), aSdu.getOriginatorAddress(), aSdu.getCommonAddress(),
                    aSdu.getInformationObjects());
        }
        connection.sendPacket(asduToCC);
        logEvent("DELIVERED asduToCC = " + asduToCC);
    }

    public static void setConnection(CCConnection connection) {
        CommandHandler.connection = connection;
    }
}
