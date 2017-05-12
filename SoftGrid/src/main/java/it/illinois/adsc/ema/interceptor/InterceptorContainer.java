package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by Edwin on 11-May-17.
 *
 * This class is container which will initialize list of interceptors defined in config file
 * and running them in the sequence defined in InterceptorFactory class. Processed ASdu package
 * will be returned to caller class
 */
public class InterceptorContainer {

    InterceptorListObject currentNode;

    public ASdu RunInterceptors(ASdu aSdu) {

        //Initialize interceptors and return the reference to first interceptor
        currentNode = InterceptorFactory.initInterceptors();

        //Run interceptor on the root, the root will call next interceptor recursively
        aSdu = currentNode.intercepts(aSdu);

        return aSdu;
    }
}
