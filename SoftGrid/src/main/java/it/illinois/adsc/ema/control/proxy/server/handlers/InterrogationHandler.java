package it.illinois.adsc.ema.control.proxy.server.handlers;

import it.illinois.adsc.ema.control.proxy.context.ProxyContextFactory;
import it.illinois.adsc.ema.control.proxy.infor.InformationASduBridge;
import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.util.DeviceType;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;

import java.util.List;

/**
 * Created by prageethmahendra on 16/1/2017.
 */
public class InterrogationHandler extends CommandHandler {
    private static InterrogationHandler interrogationHandler;

    public InterrogationHandler() {
        typeid = TypeId.C_IC_NA_1;
        init();
    }

    private void init() {
        proxyContext = ProxyContextFactory.getInstance().getProxyContext(this);
    }

    @Override
    public void handle(ASdu aSdu) {
        ProxyInformation proxyInformation = proxyContext.getIntegrationData(aSdu.getCommonAddress());
        if (proxyInformation.getDeviceType() == DeviceType.ROOT) {
            for (ProxyInformation information : proxyInformation.getDeviceInfor()) {
                deliver(aSdu, InformationASduBridge.getInformationObject(information), InformationASduBridge.getASDUTypeID(information));
            }
        } else {
            deliver(aSdu, InformationASduBridge.getInformationObject(proxyInformation), InformationASduBridge.getASDUTypeID(proxyInformation));
        }
    }

    private void deliver(ASdu aSdu, List<InformationObject> informationObject, TypeId asduTypeID) {

    }

    public static InterrogationHandler getInstance() {
        if (interrogationHandler == null) {
            interrogationHandler = new InterrogationHandler();
        }
        return interrogationHandler;
    }
}
