package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

/**
 * Created by Edwin on 09-May-17.
 *
 * This class defined node in the Interceptor linked list which will be created and
 * iterated by InterceptorFactory class
 */
public class InterceptorNode implements InterceptorListObject {
    private Interceptor curInterceptor;

    private InterceptorListObject next;
    private InterceptorListObject previous;

    //Default constructor
    public InterceptorNode() {
        curInterceptor = null;
    }

    public InterceptorNode(Interceptor curInterceptor)
    {
        this.curInterceptor = curInterceptor;
    }

    public Interceptor getCurInterceptor() {
        return curInterceptor;
    }

    public void setCurInterceptor(Interceptor curInterceptor) {
        this.curInterceptor = curInterceptor;
    }

    @Override
    public InterceptorListObject getNextInterceptor() {
        return next;
    }

    @Override
    public InterceptorListObject getPreviousInterceptor() {
        return previous;
    }

    @Override
    public void setNextInterceptor(InterceptorListObject next) {
        this.next = next;
    }

    @Override
    public void setPreviousInterceptor(InterceptorListObject previous) {
        this.previous = previous;
    }

    //Calling current interceptor to process ASdu package and passed processed ASdu to next node
    public ASdu intercepts(ASdu asDu)
    {
        ASdu interceptedASdu = curInterceptor.intercept(asDu);

        if (next != null)
            interceptedASdu = next.intercepts(interceptedASdu);

        return interceptedASdu;
    }
}
