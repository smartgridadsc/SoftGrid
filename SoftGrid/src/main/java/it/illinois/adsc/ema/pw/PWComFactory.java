package it.illinois.adsc.ema.pw;

import it.illinois.adsc.ema.pw.com4j.PWCom_com4j;
import it.illinois.adsc.ema.pw.ied.pwcom.PWCom;
import it.illinois.adsc.ema.pw.ied.pwcom.PWComAPI;

/**
 * Created by prageethmahendra on 9/6/2016.
 */
public class PWComFactory {

    public static PWComAPI getSingletonPWComInstance() {
        return getSingletonPWComInstance(PWComType.COM4J);
    }

    private static PWComAPI getSingletonPWComInstance(PWComType pwcomType) {
        switch (pwcomType) {
            case COM4J:
                return PWCom_com4j.getInstance();
            case JACOB:
                return PWCom.getInstance();
            default:
                return null;
        }
    }
}
