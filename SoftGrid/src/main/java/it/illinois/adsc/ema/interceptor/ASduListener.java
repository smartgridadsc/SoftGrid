package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by Edwin on 16-May-17.
 *
 * Interface for all classes which want to process incoming ASdu
 *
 */
public interface ASduListener {
    public ASdu ASduReceived(ASdu aSdu);
}
