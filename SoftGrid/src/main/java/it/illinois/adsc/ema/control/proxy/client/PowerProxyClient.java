package it.illinois.adsc.ema.control.proxy.client;

import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;

import java.util.List;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public interface PowerProxyClient {
    public List<ProxyInformation> interrogationRequest();
    public boolean handleControlCommand(int qualifier, Object valueObject);
    public int getIedID();
    public boolean isConnectedToBus();
}
