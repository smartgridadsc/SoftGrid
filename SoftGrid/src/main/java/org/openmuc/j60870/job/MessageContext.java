package org.openmuc.j60870.job;

import it.illinois.adsc.ema.control.proxy.server.GatewayConListener;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;

/**
 * Created by prageethmahendra on 18/7/2016.
 */
public class MessageContext {

    private Connection connection;
    private ASdu aSdu;
    private GatewayConListener gatewayConListener;
    private ContextState state = ContextState.REQUEST;
    private boolean securityEnabled;

    public MessageContext(Connection connection, ASdu aSdu, GatewayConListener gatewayConListener, ContextState state) {
        this.connection = connection;
        this.aSdu = aSdu;
        this.gatewayConListener = gatewayConListener;
        if (state != null) {
            this.state = state;
        }
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ASdu getaSdu() {
        return aSdu;
    }

    public void setaSdu(ASdu aSdu) {
        this.aSdu = aSdu;
    }

    public GatewayConListener getGatewayConListener() {
        return gatewayConListener;
    }

    public ContextState getState() {
        return state;
    }

    public void setState(ContextState state) {
        this.state = state;
    }

    public void setGatewayConListener(GatewayConListener gatewayConListener) {
        this.gatewayConListener = gatewayConListener;
    }
}
