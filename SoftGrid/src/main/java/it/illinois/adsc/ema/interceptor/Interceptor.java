package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by prageeth.g on 10/4/2017.
 *
 * This class is interface of the Interceptor. Interceptor will receive ASdu package,
 * doing package processing (adding delay, logging, etc) and then returned processed ASDu
 * before being passed into substation IED server
 */

public interface Interceptor {
    public ASdu intercept(ASdu aSdu);
}
