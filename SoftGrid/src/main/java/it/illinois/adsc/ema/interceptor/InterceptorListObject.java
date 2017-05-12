package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by prageethmahendra on 5/5/2017.
 *
 * This class is interface for individual interceptor object in the linked list.
 * Developer need to implement this interface to create node
 */
public interface InterceptorListObject {
    InterceptorListObject getNextInterceptor();
    InterceptorListObject getPreviousInterceptor();

    void setNextInterceptor(InterceptorListObject next);
    void setPreviousInterceptor(InterceptorListObject prev);

    ASdu intercepts(ASdu aSdu);
}
