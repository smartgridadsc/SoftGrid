package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

import java.util.concurrent.Callable;

/**
 * Created by Edwin on 15-May-17.
 */
public class ASduThread implements Callable {
    ASdu curASdu;

    InterceptorContainer curContainer;

    public ASduThread(ASdu curASdu, InterceptorContainer curContainer){
        this.curASdu = curASdu;
        this.curContainer = curContainer;
    }


    @Override
    public ASdu call() throws Exception {
        curASdu = curContainer.RunInterceptors(curASdu);

        return curASdu;
    }
}
