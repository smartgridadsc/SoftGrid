package it.illinois.adsc.ema.interceptor;

import it.illinois.adsc.ema.control.proxy.server.ProxyServer;
import org.openmuc.j60870.ASdu;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Edwin on 11-May-17.
 *
 * This class is container which will initialize list of interceptors defined in config file
 * and running them in the sequence defined in InterceptorFactory class. Processed ASdu package
 * will be returned to caller class
 */
public class InterceptorContainer implements ASduListener {

    private static InterceptorContainer curContainer = null;

    private static InterceptorFactory curFactory = null;

    private static ExecutorService executorService = null;

    private static InterceptorListObject currentNode = null;

    private InterceptorContainer() {


        //Set the configuration parameter
        curFactory = new InterceptorFactory();

        //Initialize interceptors and return the reference to first interceptor
        currentNode = curFactory.initInterceptors();
    }

    public void init() {
        ProxyServer.getInstance().addASduListeners(this);
    }

    public static InterceptorContainer getInstance() {
        if (curContainer == null) {
            curContainer = new InterceptorContainer();
            executorService = Executors.newFixedThreadPool(100);
        }
        return curContainer;
    }

    @Override
    public ASdu ASduReceived(ASdu aSdu) {
        try {
            ASduThread curASduThread = new ASduThread(aSdu, currentNode);
            Future<ASdu> fASdu = executorService.submit(curASduThread);
            aSdu = fASdu.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        return aSdu;
    }


}
