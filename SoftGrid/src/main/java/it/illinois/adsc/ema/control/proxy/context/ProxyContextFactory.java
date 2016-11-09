package it.illinois.adsc.ema.control.proxy.context;

import it.illinois.adsc.ema.control.proxy.client.PowerProxyClient;
import it.illinois.adsc.ema.control.proxy.server.PowerProxyServer;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public class ProxyContextFactory {
    private static ProxyContextFactory proxyContextFactory = null;
    private static SimpleProxyContext proxyContext = null;

    private ProxyContextFactory() {
        initProxyContext();
    }

    public static ProxyContextFactory getInstance() {
        if (proxyContextFactory == null) {
            proxyContextFactory = new ProxyContextFactory();
            proxyContext = new SimpleProxyContext();
        }
        return proxyContextFactory;
    }


    private synchronized void initProxyContext() {
        if (proxyContext != null) {
            proxyContext = new SimpleProxyContext();
        }
    }

    public ProxyServerContext getProxyContext(PowerProxyServer proxyServer) {
        if (proxyContext != null) {
            proxyContext.registerProxyServer(proxyServer);
        }
        return proxyContext;
    }

    public ProxyClientContext getProxyContext(int iedID, PowerProxyClient proxyClient) {
        if (proxyContext != null) {
            proxyContext.registerProxyClient(iedID, proxyClient);
        }
        return proxyContext;
    }
}
