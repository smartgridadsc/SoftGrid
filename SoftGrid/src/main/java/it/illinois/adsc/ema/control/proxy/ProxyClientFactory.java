package it.illinois.adsc.ema.control.proxy;

import it.illinois.adsc.ema.control.IEDDataSheetHandler;
import it.illinois.adsc.ema.control.ied.pw.PWModelDetails;
import it.illinois.adsc.ema.control.proxy.client.SubstationProxyClient;
import org.openmuc.openiec61850.ServiceError;

import java.io.IOException;

/**
 * Created by prageethmahendra on 16/2/2016.
 */
public class ProxyClientFactory {
    private static int proxyClientAddressCount = 1;
    private static SubstationProxyClient substationProxyClient = null;

    public static void startNormalProxy(PWModelDetails modelDetails) throws ServiceError, IOException {
        if (IEDDataSheetHandler.isProxyConnectionAllowed(modelDetails.getPortNumber())) {
            substationProxyClient = new SubstationProxyClient(modelDetails.getPortNumber() - 10003);
            substationProxyClient.init(ProxyType.NORMAL);
            substationProxyClient.startProxy(modelDetails);
        }
    }

    public static void startSecurityEnabledProxy(PWModelDetails modelDetails) throws ServiceError, IOException {
        substationProxyClient = new SubstationProxyClient(modelDetails.getPortNumber() - 10003);
        substationProxyClient.init(ProxyType.SECURITY_ENABLED);

        substationProxyClient.startProxy(modelDetails);
    }

    public static void killAll() {
        if (substationProxyClient != null) {
            substationProxyClient.stop();
        }
    }
}
