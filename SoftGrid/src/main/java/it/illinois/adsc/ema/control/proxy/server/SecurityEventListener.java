package it.illinois.adsc.ema.control.proxy.server;

import org.openmuc.j60870.ASdu;

/**
 * Created by SmartPower on 1/6/2016.
 */
public interface SecurityEventListener {
    public void readyToExecute(ASdu aSdu);

    void readyToExecute(ASdu aSdu, int qualifier, Object newState);
}
