package it.illinois.adsc.ema.interceptor;

import it.illinois.adsc.ema.interceptor.Interceptor;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.openmuc.j60870.ASdu;

/**
 * Created by prageeth.g on 30/5/2017.
 */
public class LoggingInterceptor2 implements Interceptor {
    private static Logger logger = null;
    @Override
    public ASdu intercept(ASdu aSdu) {
        if(logger == null)
        {
            logger = Logger.getLogger(getClass().getName());
        }
        logger.info("Dummy Intercepted ASDU = " + aSdu.toString());;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return aSdu;
    }
}
