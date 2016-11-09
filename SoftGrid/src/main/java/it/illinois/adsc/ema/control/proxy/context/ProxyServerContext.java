package it.illinois.adsc.ema.control.proxy.context;

import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.server.PowerProxyServer;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public interface ProxyServerContext {
    public void registerProxyServer(PowerProxyServer proxyServer);
    public ProxyInformation getIntegrationData(int iedID);

    public boolean handleControlCommand(int iedID, int qualifier, boolean state);
    public boolean handleControlCommand(int iedID, int qualifier, Object valueObject);
}
