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

    private static InterceptorContainer curContainer = null;

    private static InterceptorFactory curFactory = null;

    private InterceptorListObject currentNode;

    public static InterceptorContainer getInstance() {
        if (curContainer == null) {
            curContainer = new InterceptorContainer();
        }
        return curContainer;
    }

    private InterceptorContainer() {
        //Set the configuration parameter
        curFactory = new InterceptorFactory();

        //Initialize interceptors and return the reference to first interceptor
        currentNode = curFactory.initInterceptors();
    }

    public synchronized ASdu RunInterceptors(ASdu aSdu) {

        //Run interceptor on the root, the root will call next interceptor recursively
        if (currentNode != null) {
            aSdu = currentNode.intercepts(aSdu);
        }

        return aSdu;
    }
}
