package org.openmuc.j60870.job;

import it.illinois.adsc.ema.control.proxy.server.GatewayConListener;

/**
 * Created by prageethmahendra on 18/7/2016.
 */
public class SecureConfirmationJob implements Job{

    private GatewayConListener gatewayConListener;

    public SecureConfirmationJob(GatewayConListener gatewayConListener) {
        this.gatewayConListener = gatewayConListener;
    }

    @Override
    public void execute(MessageContext messageContext) {
        this.gatewayConListener.newAsduFromCC(messageContext.getaSdu());
    }
}
