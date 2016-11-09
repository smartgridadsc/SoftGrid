package it.illinois.adsc.ema.pw.ied.pwcom;

/**
* Created by prageethmahendra on 21/1/2016.
*/
public class PWComFactory {
    public static PWComAPI getPWComBridgeIterface()
    {
        PWCom pwCom =  PWCom.getInstance();
        return pwCom;
    }
}
