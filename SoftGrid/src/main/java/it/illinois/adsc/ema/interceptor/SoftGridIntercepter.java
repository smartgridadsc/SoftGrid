package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by prageeth.g on 10/4/2017.
 */
public interface SoftGridIntercepter {
    public ASdu intercept(ASdu aSdu);
}
