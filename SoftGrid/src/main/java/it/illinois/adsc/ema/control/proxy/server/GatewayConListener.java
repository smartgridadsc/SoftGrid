package it.illinois.adsc.ema.control.proxy.server;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.IEC60870D104Receiver;

/**
 * Created by prageethmahendra on 14/6/2016.
 */
public interface GatewayConListener{
    public void ccConnectionClosed(IEC60870D104Receiver iec60870D104Receiver);
    public void newAsduFromCC(ASdu aSdu);
}
