package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by prageeth.g on 29/5/2017.
 */
public interface InterceptorListener {
    public void interceptCompleted(ASdu aSdu, ASduThread aSduThread);
}
