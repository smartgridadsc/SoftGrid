package it.illinois.adsc.ema.control.proxy.context;

import it.illinois.adsc.ema.control.proxy.client.PowerProxyClient;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public interface ProxyClientContext {
    void registerProxyClient(int iedID, PowerProxyClient proxyClient);
}
