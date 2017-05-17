package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

import java.util.concurrent.Callable;

/**
 * Created by Edwin on 15-May-17.
 */
public class ASduThread implements Callable {
    ASdu curASdu;

    InterceptorListObject curNode;

    public ASduThread(ASdu curASdu, InterceptorListObject curNode){
        this.curASdu = curASdu;
        this.curNode = curNode;
    }


    @Override
    public ASdu call() throws Exception {

        //Run interceptor on the root, the root will call next interceptor recursively
        if (curNode != null) {
            curASdu = curNode.intercepts(curASdu);
        }

        return curASdu;
    }
}
